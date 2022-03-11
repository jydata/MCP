package com.jiuye.mcp.param.enums;

/**
 * @author kevin
 */
public enum DeleteFlagEnum {

    YES(0, "Deleted"),
    NO(1, "Undeleted");

    private int code;
    private String message;

    DeleteFlagEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
