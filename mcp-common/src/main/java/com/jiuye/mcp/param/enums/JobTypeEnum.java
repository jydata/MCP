package com.jiuye.mcp.param.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * JobType
 *
 * @author kevin
 * @date 2018-09-30
 */
public enum JobTypeEnum {

    INCREMENT("increment", "增量同步"),
    FULL("full", "全量同步"),
    METADATA("metadata","元数据同步"),
    DDL("ddl", "批量DDL转换");

    private String code;
    private String message;

    JobTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Map<String, String> maps = new HashMap<>();
    static{
        maps.put(INCREMENT.getCode(), INCREMENT.getMessage());
        maps.put(FULL.getCode(), FULL.getMessage());
        maps.put(METADATA.getCode(), METADATA.getMessage());
        maps.put(DDL.getCode(), DDL.getMessage());
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
