package com.jiuye.mcp.server.dao.sys;

import com.jiuye.mcp.server.model.meta.DictEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * sys_dict mapper
 *
 * @author kevin
 * @date 2018-12-25
 */
@Component
public interface SysDictMapper {

    /**
     * 查询数据库名称
     * @param entity
     * @return
     */
    List<DictEntity> queryDatabaseName(DictEntity entity);

    /**
     * 查询去重后的名称集合
     * @param entity
     * @return
     */
    List<DictEntity> queryDistinctNameList(DictEntity entity);

    /**
     * 查询规则列表
     * @return
     */
    List<DictEntity> queryRulesType();

    /**
     * 根据flag查询name列表
     * @param flag
     * @return
     */
    List<String> queryDistinctName(int flag);

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    int save(DictEntity entity);

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    int update(DictEntity entity);

}
