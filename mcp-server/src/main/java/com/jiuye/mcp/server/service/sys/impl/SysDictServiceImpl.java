package com.jiuye.mcp.server.service.sys.impl;

import com.jiuye.mcp.server.dao.sys.SysDictMapper;
import com.jiuye.mcp.server.model.meta.DictEntity;
import com.jiuye.mcp.server.service.sys.ISysDictService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @author kevin
 * 
 */
@Service
public class SysDictServiceImpl implements ISysDictService {

	private static final Logger logger = Logger.getLogger(SysDictServiceImpl.class.getName());

    @Autowired
    private SysDictMapper sysDictMapper;

    @Override
    public List<DictEntity> queryRulesType() {
        return sysDictMapper.queryRulesType();
    }
}
