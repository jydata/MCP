package com.jiuye.mcp.server.service.sys;

import com.jiuye.mcp.server.model.meta.DictEntity;

import java.util.List;

/**
 * 
 * @author kevin
 * 
 */
public interface ISysDictService {

    /**
     * 加载Rules Type
     *
     * @return
     */
    List<DictEntity> queryRulesType();
}
