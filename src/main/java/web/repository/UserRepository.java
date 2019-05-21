package web.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import web.domain.User;


@Repository
public class UserRepository {

    @Autowired
    private MongoTemplate template;

    private final static String COLLECTION_NAME = "user";

    public User checkPwd(User user){
        Query query = new Query(Criteria.where("username").is(user.getUsername())
                .and("password").is(user.getPassword()));
        User dbUser = template.findOne(query, User.class, COLLECTION_NAME);
        return dbUser;
    }


}
