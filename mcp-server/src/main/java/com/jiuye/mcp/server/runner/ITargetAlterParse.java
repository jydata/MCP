package com.jiuye.mcp.server.runner;

import java.util.Map;

/**
 * @author jepson
 * @date 2018/10/24 1:47 PM
 */
public interface ITargetAlterParse {

    String alterSQLParse(Map<String, String> ddlRulesMap,String sourceDatabase,  String sql) throws Exception;
}
