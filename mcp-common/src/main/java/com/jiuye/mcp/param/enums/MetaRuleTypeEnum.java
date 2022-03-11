package com.jiuye.mcp.param.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * meta rule type enum
 *
 * @author kevin
 * @date 2018-09-30
 */
public enum MetaRuleTypeEnum {

    DEFAULT("Default", "Only create a table with the same name."),
    MERGE_TABLE("Merge table", "All table name increases db_name prefix."),
    MERGE_ADD_COLUMN("Merge table and add column", "Only create a table with the same name, and add new column."),
    RESTORE("Restore", "Restore the database table with the specified rule name.");

    private String code;
    private String message;

    MetaRuleTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Map<String, String> maps = new HashMap<>();
    static{
        maps.put(DEFAULT.getCode(), DEFAULT.getMessage());
        maps.put(MERGE_TABLE.getCode(), MERGE_TABLE.getMessage());
        maps.put(MERGE_ADD_COLUMN.getCode(), MERGE_ADD_COLUMN.getMessage());
        maps.put(RESTORE.getCode(), RESTORE.getMessage());
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
