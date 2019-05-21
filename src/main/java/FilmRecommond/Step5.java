package FilmRecommond;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import web.domain.RecommendList;
import web.repository.RecommendListRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/*
对MapRecude的结果集进行清理
 */
public class Step5 {


    public static class Step5SaveInMongo extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

        private final static LinkedList<BasicDBObject> list = new LinkedList<>();

        @Override
        public void map(LongWritable key, Text values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
            String[] tokens = Recommend.DELIMITER.split(values.toString());
            BasicDBObject basi = new BasicDBObject();
            basi.put("uid", tokens[0]);
            basi.put("fileName", tokens[1]);
            basi.put("weight", Double.valueOf(tokens[2]).longValue());
            list.add(basi);
            if (list.size() > 2000) {
                this.insertInMongo();
            }

        }

        private void insertInMongo() {
            MongoClient mClient = new MongoClient("172.20.10.8");
            DB db = mClient.getDB("hadoop_user");
            DBCollection collection = db.getCollection("recommend_list");
            DBCollection userCollection = db.getCollection("user_history");
            for (BasicDBObject i : list) {
                BasicDBObject basiTemp = new BasicDBObject();
                basiTemp.put("uid", i.get("uid"));
                basiTemp.put("lastProgramName", i.get("fileName"));
                if (userCollection.findOne(basiTemp) == null && collection.findOne(basiTemp) == null) {
                    collection.save(i);
                }
            }
            list.clear();
        }
    }

    public static void run(String path) throws IOException {
        System.out.println("Step5开始储存进mongo...");
        JobConf conf = Recommend.config();
        HdfsDAO hdfs = new HdfsDAO(Recommend.HDFS, conf);
        String blankDir = "/user/blank";
        hdfs.rmr(blankDir);
        String input = path;
        String output = blankDir;
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Step5SaveInMongo.class);
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
        FileInputFormat.setInputPaths(conf, new Path(input));
        FileOutputFormat.setOutputPath(conf, new Path(output));
        RunningJob job = JobClient.runJob(conf);
        while (!job.isComplete()) {
            job.waitForCompletion();
        }
        System.out.println("=======Step5储存进mongo结束========");
    }

}