package com.jiuye.mcp.param.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * job scheduler code
 *
 * @author kevin
 * @date 2018-10-24
 */
public enum JobSchedulerEnum {

    INIT(100, "初始"),
    RUNNING(300, "运行"),
    SUCCESS(200, "成功"),
    FAIL(500, "失败");

    private int code;
    private String message;

    JobSchedulerEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Map<Integer, String> maps = new HashMap<>();
    static{
        maps.put(INIT.getCode(), INIT.getMessage());
        maps.put(RUNNING.getCode(), RUNNING.getMessage());
        maps.put(SUCCESS.getCode(), SUCCESS.getMessage());
        maps.put(FAIL.getCode(), FAIL.getMessage());
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
