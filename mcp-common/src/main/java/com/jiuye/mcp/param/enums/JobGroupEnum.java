package com.jiuye.mcp.param.enums;

/**
 * jobgroup
 *
 * @author kevin
 * @date 2018-10-30
 */
public enum JobGroupEnum {

    TRIGGER("MCP_SCHEDULER_TRIGGERGROUP"),
    ONCE("MCP_SCHEDULER_JOBGROUP");

    private String code;

    JobGroupEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
