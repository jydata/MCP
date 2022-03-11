package com.jiuye.mcp.param.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * JobStatus
 *
 * @author kevin
 * @date 2018-09-30
 */
public enum JobStatusEnum {

    INIT("init", "初始化"),
    WAIT("wait", "等待"),
    READY("ready", "就绪"),
    RUNNING("running", "执行中"),
    FAIL("fail", "失败"),
    SUCCESS("success", "成功");
//    WARNING("warning", "警告");

    private String code;
    private String message;

    JobStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Map<String, String> maps = new HashMap<>();
    static{
        maps.put(INIT.getCode(), INIT.getMessage());
        maps.put(WAIT.getCode(), WAIT.getMessage());
        maps.put(READY.getCode(), READY.getMessage());
        maps.put(RUNNING.getCode(), RUNNING.getMessage());
        maps.put(FAIL.getCode(), FAIL.getMessage());
        maps.put(SUCCESS.getCode(), SUCCESS.getMessage());
//        maps.put(WARNING.getCode(), WARNING.getMessage());
    }
    public static Map<String,String> getMap(){
        return maps;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
