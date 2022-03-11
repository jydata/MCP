package com.jiuye.mcp.param.enums;

/**
 * @author kevin
 */
public enum DeleteStatusEnum {

    YES("0", "Deleted"),
    NO("1", "Undeleted");

    private String code;
    private String message;

    DeleteStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
