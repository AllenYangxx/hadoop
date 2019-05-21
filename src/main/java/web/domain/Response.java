package web.domain;

import lombok.Data;

@Data
public class Response<T> {

    private String code;

    private String message;

    private T data;

    public static Response error(){
        Response response = new Response();
        response.setCode("0");
        response.setMessage("错误");
        return response;
    }

    public static Response suc(Object data){
        Response response = new Response();
        response.setCode("1");
        response.setMessage("成功");
        response.setData(data);
        return response;

    }

}
