package web.domain;

import lombok.Data;

@Data
public class User {

    private Long uid;

    private String username;

    private String password;

    private String identity;

}
