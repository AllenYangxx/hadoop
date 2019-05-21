package web.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import web.domain.Page;
import web.domain.RecommendList;
import web.domain.UserHistory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Repository
public class UserHistoryRepository {

    @Autowired
    private MongoTemplate template;

    private static BufferedWriter bw;

    private final static String COLLECTION_NAME = "user_history";

    public void DataClean() {

        System.out.println("=============清理数据开始===========");
        DBCollection collection = template.getCollection(COLLECTION_NAME);
        DBCursor dc = collection.find();
        while (dc.hasNext()) {
            boolean IsUpdate = false;
            BasicDBObject basi = (BasicDBObject) dc.next();
            //清理视频名字有“，”号的并修改成中文的逗号
            String lastProgramName = (String) basi.get("lastProgramName");
            //节目集为空的时候
            if (lastProgramName.isEmpty()) {
                IsUpdate = true;
                lastProgramName = "未知" + basi.get("uid");
                basi.replace("lastProgramName", lastProgramName);
            }
            //清理有的节目集包含:
            if (StringUtils.contains(lastProgramName, ":")) {
                IsUpdate = true;
                lastProgramName = lastProgramName.replace(":", "：");
                basi.replace("lastProgramName", lastProgramName);
            }
            if (StringUtils.contains(lastProgramName, " ")) {
                IsUpdate = true;
                lastProgramName = StringUtils.deleteWhitespace(lastProgramName);
                basi.replace("lastProgramName", lastProgramName);
            }
            if (lastProgramName.indexOf(",") != -1) {
                IsUpdate = true;
                lastProgramName = lastProgramName.replace(",", "，");
                basi.replace("lastProgramName", lastProgramName);
            }
            //处理seconds为空的情况,小于0
            if ((basi.get("seconds").toString()).isEmpty() ||
                    (Long) basi.get("seconds") < 0) {
                IsUpdate = true;
                basi.replace("seconds", Long.valueOf(0));
            }
            //处理totalTime为空的情况,小于0
            if ((basi.get("totalTime").toString()).isEmpty() ||
                    (Long) basi.get("totalTime") < 0) {
                IsUpdate = true;
                basi.replace("totalTime", basi.get("seconds"));
            }
            //处理seconds太大和totalTime太大的情况，最大为10000,处理-1的情况
            Long maxTime = Long.valueOf(10000);
            if (((Long) basi.get("totalTime")) > maxTime || ((Long) basi.get("seconds")) > maxTime) {
                IsUpdate = true;
                Long totalTime = (Long) basi.get("totalTime");
                Long seconds = (Long) basi.get("seconds");
                while (totalTime > maxTime || seconds > maxTime) {
                    totalTime = totalTime / 10;
                    seconds = seconds / 10;
                    if (seconds < 1) {
                        seconds = Long.valueOf(1);
                    }
                    if (totalTime < 1) {
                        totalTime = seconds;
                    }
                }
                basi.replace("seconds", seconds);
                basi.replace("totalTime", totalTime);
            }
            if (IsUpdate) {
                BasicDBObject searchObj = new BasicDBObject();
                searchObj.put("_id", basi.get("_id"));
                collection.update(searchObj, basi);
            }
        }
        System.out.println("=============清理数据完成===========");
    }

    public void WirteToCsv(String path) {
        try {
            System.out.println("=============写入开始===========");
            bw = new BufferedWriter(new FileWriter(new File(path)));
            DBCollection dd = template.getCollection(COLLECTION_NAME);
            DBCursor dc = dd.find();
            while (dc.hasNext()) {
                BasicDBObject basi = (BasicDBObject) dc.next();
                StringBuffer sb = new StringBuffer();
                sb.append(basi.get("uid") + ",");
                sb.append(basi.get("lastProgramName") + ",");
                Long seconds = (Long) basi.get("seconds");
                Long totalTime = (Long) basi.get("totalTime");
                Double watchPercent = seconds == 0 || totalTime == 0 ?
                        1 : seconds.doubleValue() / totalTime.doubleValue() * 10000;
                if (seconds > totalTime) {
                    watchPercent = Double.valueOf(10000);
                }
                sb.append(watchPercent.longValue());
                bw.write(sb.toString() + "\r\n");
            }
            bw.flush();
            System.out.println("=============写入完成===========");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean checkUidAndFile(RecommendList recommendList) {
        Query query = new Query(Criteria.where("uid").is(recommendList.getUid())
                .and("lastProgramName").is(recommendList.getFileName()));
        if (template.exists(query, COLLECTION_NAME)) {
            return true;
        }
        return false;
    }

    public Page searchByUidPage(Long uid, Integer index, Integer size) {
        Long count;
        List<UserHistory> list;
        if (uid == null) {
            Query query = new Query();
            list = template.find(query.skip(size * index).limit(size), UserHistory.class, COLLECTION_NAME);
            count = template.count(query, UserHistory.class, COLLECTION_NAME);
        } else {
            Query query = new Query(Criteria.where("uid").is(uid));
            list = template.find(query.skip(size * index).limit(size), UserHistory.class, COLLECTION_NAME);
            count = template.count(query, UserHistory.class, COLLECTION_NAME);
        }
        //创建分页实体对象
        return Page.suc(count, list);
    }
}
