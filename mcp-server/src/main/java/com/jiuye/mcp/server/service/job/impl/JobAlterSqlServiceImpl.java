/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuye.mcp.server.service.job.impl;

import com.jiuye.mcp.server.dao.job.JobAlterSqlMapper;
import com.jiuye.mcp.server.service.job.IJobAlterSqlService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author kevin
 * 
 */
@Service
public class JobAlterSqlServiceImpl implements IJobAlterSqlService {

	private static final Logger logger = Logger.getLogger(JobAlterSqlServiceImpl.class.getName());

	@Autowired
	private JobAlterSqlMapper jobAlterSqlMapper;


    @Override
    public String queryTargetOperateDDL(Long routeId, String srcDbName, String srcTableName) {
        return jobAlterSqlMapper.queryTargetOperateDDL(routeId, srcDbName, srcTableName);
    }
}
