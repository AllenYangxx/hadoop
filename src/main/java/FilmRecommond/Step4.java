package FilmRecommond;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

public class Step4 {

    public static class Step4_PartialMultiplyMapper extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, Text> {
        private final static IntWritable k = new IntWritable();
        private final static Text v = new Text();

        private final static Map<String, List> cooccurrenceMatrix = new HashMap<String, List>();

        @Override
        public void map(LongWritable key, Text values, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
            String[] tokens = Recommend.DELIMITER.split(values.toString());
            String[] v1 = tokens[0].split(":");
            String[] v2 = tokens[1].split(":");

            if (v1.length > 1) {// cooccurrence
                String itemID1 = v1[0];
                String itemID2 = v1[1];
                int num = Integer.parseInt(tokens[1]);

                List list = null;
                if (!cooccurrenceMatrix.containsKey(itemID1)) {
                    list = new ArrayList();
                } else {
                    list = cooccurrenceMatrix.get(itemID1);
                }
                list.add(new Cooccurrence(itemID1, itemID2, num));
                cooccurrenceMatrix.put(itemID1, list);
            }

            if (v2.length > 1) {// userVector
                String itemID = tokens[0];
                int userID = Integer.parseInt(v2[0]);
                double pref = Double.parseDouble(v2[1]);
                k.set(userID);
                for (Object co : cooccurrenceMatrix.get(itemID)) {
                    Cooccurrence c = (Cooccurrence) co;
                    v.set(c.getItemID2() + "," + pref * c.getNum());
                    output.collect(k, v);
                }

            }
        }
    }

    public static class Step4_AggregateAndRecommendReducer extends MapReduceBase implements Reducer<IntWritable, Text, IntWritable, Text> {
        private final static Text v = new Text();

        @Override
        public void reduce(IntWritable intWritable, Iterator<Text> iterator, OutputCollector<IntWritable, Text> outputCollector, Reporter reporter) throws IOException {
            Map<String, Double> result = new HashMap<String, Double>();
            while (iterator.hasNext()) {
                String[] str = iterator.next().toString().split(",");
                if (result.containsKey(str[0])) {
                    result.put(str[0], result.get(str[0]) + Double.parseDouble(str[1]));
                } else {
                    result.put(str[0], Double.parseDouble(str[1]));
                }
            }
            Iterator iter = result.keySet().iterator();
            while (iter.hasNext()) {
                String itemID = iter.next().toString();
                double score = result.get(itemID);
                v.set(itemID + "," + score);
                outputCollector.collect(intWritable, v);
            }
        }
    }

    public static void run(Map<String, String> path) throws IOException {
        JobConf conf = Recommend.config();

        String input1 = path.get("Step4Input1");
        String input2 = path.get("Step4Input2");
        String output = path.get("Step4Output");

        HdfsDAO hdfs = new HdfsDAO(Recommend.HDFS, conf);
        hdfs.rmr(output);

        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Step4_PartialMultiplyMapper.class);
        conf.setCombinerClass(Step4_AggregateAndRecommendReducer.class);
        conf.setReducerClass(Step4_AggregateAndRecommendReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(input1), new Path(input2));
        FileOutputFormat.setOutputPath(conf, new Path(output));

        RunningJob job = JobClient.runJob(conf);
        while (!job.isComplete()) {
            job.waitForCompletion();
        }
    }
}

