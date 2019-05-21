package web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.*;
import web.domain.Page;
import web.domain.UserHistory;
import web.repository.UserHistoryRepository;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "userHistory")
public class UserHistoryController {

    @Autowired
    private UserHistoryRepository userHistoryRepository;

    @GetMapping(value = "search")
    public Page<List<UserHistory>> searchByUidPage(Long uid, Integer index, Integer size) {

        Page<List<UserHistory>> page = userHistoryRepository.searchByUidPage(uid, index - 1, size);
        return page;

    }

}
