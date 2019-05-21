package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.domain.Response;
import web.domain.Test;
import web.domain.User;
import web.repository.TestRepository;
import web.repository.UserRepository;

import java.util.List;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "login")
    public Response<User> login(User user) {
        User dbUser = userRepository.checkPwd(user);
        if (dbUser != null) {
            return Response.suc(dbUser);
        } else {
            return Response.error();
        }
    }

    @Autowired
    private TestRepository testRepository;

    @RequestMapping(value = "test")
    public List<Test> test(){
        return testRepository.test();
    }

}
