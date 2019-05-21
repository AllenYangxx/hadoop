package web.controller;


import FilmRecommond.Recommend;
import FilmRecommond.Step5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.domain.Page;
import web.domain.RecommendList;
import web.domain.Response;
import web.repository.RecommendListRepository;
import web.repository.UserHistoryRepository;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "recommendList")
public class RecommendController {

    @Autowired
    private UserHistoryRepository userHistoryRepository;

    @Autowired
    private RecommendListRepository recommendListRepository;

    /**
     * 自动运行
     * @return
     */
    @RequestMapping(value = "run")
    public String test() {
        this.dataclean();
        this.file();
        this.startRecommend();
        return "success_run";
    }

    /**
     * 按照标准格式写入csv
     * @return
     */
    @RequestMapping(value = "file")
    public Response file() {
        try{
            Thread.sleep(2000);
//            userHistoryRepository.WirteToCsv("D:/FileTest/history.csv");
        }catch (Exception e){
            e.printStackTrace();
            return Response.error();
        }
        return Response.suc(null);
    }

    /**
     * 数据清洗
     * @return
     */
    @RequestMapping(value = "dataclean")
    public Response dataclean() {
        try{
            Thread.sleep(2000);
//            userHistoryRepository.DataClean();
        }catch(Exception e){
            e.printStackTrace();
            return Response.error();
        }
        return Response.suc(null);
    }

    /**
     * 开始运行推荐算法
     * @return
     */
    @RequestMapping(value = "startRecommendStart")
    public Response startRecommend() {
        try {
            Thread.sleep(2000);

//            Recommend.run("D:/FileTest/history.csv");
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error();
        }
        return Response.suc(null);
    }

    /**
     * 将运行完的推荐结果集储存进Mongo
     * @return
     */
    @RequestMapping(value = "saveInMongo")
    public Response saveInMongo() {
        String hadoopPath = "/user/hdfs/recommend/step4/part-00000";
        try {
            Thread.sleep(2000);
//            Step5 step5 = new Step5();
//            step5.run(hadoopPath);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error();
        }
        return Response.suc(null);
    }


    @GetMapping(value = "search")
    public Page<List<RecommendList>> search(Long uid, Integer index, Integer size) {
        Page<List<RecommendList>> page = recommendListRepository.searchByUidPage(uid, index - 1, size);
        return page;
    }

    @GetMapping(value = "findOneByUid/{uid}")
    public Response<RecommendList> findOneByUid(@PathVariable("uid") Long uid) {
        RecommendList data = recommendListRepository.findOneByUid(uid);
        if(data == null){
            return Response.error();
        }else{
            return Response.suc(data);
        }
    }

    @GetMapping(value = "recommendFilmToUser/{uid}")
    public Response<List<RecommendList>> recommendFilmToUser(@PathVariable("uid") Long uid){
        return Response.suc(recommendListRepository.recommendFilmToUser(uid));
    }

}
