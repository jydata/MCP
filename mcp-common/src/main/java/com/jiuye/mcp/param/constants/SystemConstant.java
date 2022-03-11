package com.jiuye.mcp.param.constants;
/**
 * 系统公用常量
 *
 * @author kevin
 * @date 2018-07-23
 */
public class SystemConstant {

    public static final String RESPONSE_CODE_SUCCESS = "SUCCESS";
    public static final String RESPONSE_CODE_ERROR  = "ERROR";

    public static final String SYMBOL_PATCH  = "/";
    public static final String TOKEN_NAME  = "mcp_ticket";
    // 60 * 60 * 2
    public static final int MAXINACTIVEINTERVAL  = 7200;
    public static final int MAX_REDIS_VALIDTIME  = 1800000;

}
