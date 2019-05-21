package web.domain;

import lombok.Data;

import java.util.List;

@Data
public class Page<T> {

    private String code;

    private String msg;

    private Long totalCount;

    private List<T> list;

    public static Page suc(Long totalCount, List list){
        Page page = new Page();
        page.setCode("0");
        page.setMsg("成功");
        page.setTotalCount(totalCount);
        page.setList(list);
        return page;

    }

}
