package web.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import web.domain.Test;

import java.util.List;


@Repository
public class TestRepository {


    @Autowired
    private MongoTemplate template;

    public List<Test> test(){
        Query query = new Query();
        return template.find(query.with(new Sort(Sort.Direction.DESC, "uid")),
                Test.class, "test");
    }


}
