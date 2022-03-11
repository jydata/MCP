package com.jiuye.mcp.server.dao.meta;

import com.jiuye.mcp.server.model.meta.MetaRulesEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-11-17
 */
@Component
public interface MetaRulesMapper {

    /**
     * 查询规则列表
     *
     * @return
     */
    List<MetaRulesEntity> queryList();

    /**
     * 查询对象
     * @param entity
     * @return
     */
    MetaRulesEntity query(MetaRulesEntity entity);

    /**
     * 查询规则名称列表
     *
     * @return
     */
    List<String> queryNameList();

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    int save(MetaRulesEntity entity);

    /**
     * 删除
     *
     * @param entity
     * @return
     */
    int delete(MetaRulesEntity entity);

}
