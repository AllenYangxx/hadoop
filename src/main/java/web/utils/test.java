package web.utils;

import org.apache.commons.lang.StringUtils;

public class test {
    public static void main(String[] args) {
        StringToLong();
    }
    public static void StringToLong(){
        String a = "10011.0";
        Integer b = Double.valueOf(a).intValue();
        System.out.println(b);
    }
}
