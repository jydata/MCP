package com.jiuye.mcp.server.dao.meta;

import com.jiuye.mcp.server.model.meta.MetaConvertDataTypeEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author kevin
 */
@Component
public interface MetaConvertDataTypeMapper {

    /**
     * 查询列表
     *
     * @param entity
     * @return
     */
    List<MetaConvertDataTypeEntity> queryList(MetaConvertDataTypeEntity entity);

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    MetaConvertDataTypeEntity save(MetaConvertDataTypeEntity entity);

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    MetaConvertDataTypeEntity update(MetaConvertDataTypeEntity entity);

}
