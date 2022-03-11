/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuye.mcp.server.service.job;

/**
 * 
 * @author kevin
 * 
 */
public interface IJobAlterSqlService {

    /**
     * 查询终端operate DDL信息
     * @param routeId
     * @param srcDbName
     * @param srcTableName
     * @return
     */
    String queryTargetOperateDDL(Long routeId, String srcDbName, String srcTableName);

}
