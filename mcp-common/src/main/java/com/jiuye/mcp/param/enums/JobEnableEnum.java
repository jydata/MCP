package com.jiuye.mcp.param.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * JobEnable
 *
 * @author kevin
 * @date 2018-09-30
 */
public enum JobEnableEnum {
    NO(0, "Unenabled"),
    YES(1, "Enabled");

    private int code;
    private String message;

    JobEnableEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Map<Integer, String> maps = new HashMap<>();
    static{
        maps.put(NO.getCode(), NO.getMessage());
        maps.put(YES.getCode(), YES.getMessage());
    }
    public static Map<Integer,String> getMap(){
        return maps;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
