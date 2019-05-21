package web.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection="recommend_list")
public class RecommendList {

    private String uid;

    private String fileName;

    private Long weight;

}
