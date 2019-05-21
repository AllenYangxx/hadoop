package web.repository;

import FilmRecommond.Recommend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import web.domain.Page;
import web.domain.RecommendList;

import java.util.List;

@Repository
public class RecommendListRepository {

    @Autowired
    private MongoTemplate template;

    @Autowired
    private UserHistoryRepository userHistoryRepository;

    private final static int MAX_RECOMMEND_SIZE = 20;

    private final static String COLLECTION_NAME = "recommend_list";

    public void save(RecommendList recommendList) {
        if (!userHistoryRepository.checkUidAndFile(recommendList)) {
            template.save(recommendList, COLLECTION_NAME);
        }
    }

    public Page searchByUidPage(Long uid, Integer index, Integer size) {
        Query query = new Query(Criteria.where("uid").is(uid.toString()));
        Long totalCount = template.count(query, RecommendList.class, COLLECTION_NAME);
        List<RecommendList> list = template.find(query.skip(size * index).limit(size).with(new Sort(Sort.Direction.DESC, "weight")), RecommendList.class, COLLECTION_NAME);
        return Page.suc(totalCount, list);
    }

    public RecommendList findOneByUid(Long uid) {
        return template.findOne(new Query(Criteria.where("uid").is(uid.toString())), RecommendList.class, COLLECTION_NAME);
    }

    public List recommendFilmToUser(Long uid) {
        Query query = new Query(Criteria.where("uid").is(uid.toString()));
        query.limit(MAX_RECOMMEND_SIZE);
        query.with(new Sort(Sort.Direction.DESC, "weight"));
        List<RecommendList> list = template.find(query, RecommendList.class, COLLECTION_NAME);
        //删除
        for (RecommendList temp : list) {
            Query queryTemp = new Query(Criteria.where("uid").is(temp.getUid())
                    .and("fileName").is(temp.getFileName()));
            template.remove(queryTemp, RecommendList.class, COLLECTION_NAME);
        }
        return list;
    }
}
