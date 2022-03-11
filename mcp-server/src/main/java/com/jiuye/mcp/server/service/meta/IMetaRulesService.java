package com.jiuye.mcp.server.service.meta;

import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.meta.MetaRulesEntity;

import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-11-16
 */
public interface IMetaRulesService {

    /**
     * 查询所有规则名称
     *
     * @return
     */
    List<String> queryNameList();

    /**
     * 加载Rules列表
     *
     * @return
     */
    List<MetaRulesEntity> queryList();

    /**
     * 查询Rules信息
     *
     * @param ruleName
     * @return
     */
    MetaRulesEntity queryByName(String ruleName);

    /**
     * 保存
     *
     * @param entity
     * @return
     * @throws Exception
     */
    ValidateResult save(MetaRulesEntity entity) throws Exception;

    /**
     * 删除Rules信息
     *
     * @param entity
     */
    boolean delete(MetaRulesEntity entity);

}
