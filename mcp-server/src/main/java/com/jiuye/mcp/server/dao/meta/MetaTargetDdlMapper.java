package com.jiuye.mcp.server.dao.meta;

import com.jiuye.mcp.server.model.meta.MetaTargetDdlEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 
 * @author kevin
 * 
 */
@Component
public interface MetaTargetDdlMapper {

    /**
     * query synced sql
     *
     * @param targetSchemaId
     * @param targetTableName
     * @return
     */
    List<MetaTargetDdlEntity> querySqlList(@Param("targetSchemaId")Long targetSchemaId, @Param("targetTableName") String targetTableName);

    /**
     * query
     *
     * @param entity
     * @return
     */
    MetaTargetDdlEntity query(MetaTargetDdlEntity entity);

    /**
     * query by id
     * @param id
     * @return
     */
    MetaTargetDdlEntity queryById(long id);

    /**
     * 查询HBase sql列表
     *
     * @param entity
     * @return
     */
    List<MetaTargetDdlEntity> queryList(MetaTargetDdlEntity entity);

    /**
     * 保存HBase sql
     * @param entity
     * @return
     */
    int save(MetaTargetDdlEntity entity);

    /**
     * 批量保存
     *
     * @param list
     * @return
     */
    int saveBatch(List<MetaTargetDdlEntity> list);

    /**
     * 更新执行状态
     *
     * @param entity
     * @return
     */
    int updateExecuteFlag(MetaTargetDdlEntity entity);

}
