package com.jiuye.mcp.server.dao.meta;

import com.jiuye.mcp.server.model.job.JobSyncTableEntity;
import com.jiuye.mcp.server.model.meta.MetaMysqlStatisticsEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author kevin
 */
@Component
public interface MetaMysqlStatisticsMapper {

    /**
     * 批量保存索引
     *
     * @param list
     * @return
     */
    int saveBatch(List<MetaMysqlStatisticsEntity> list);

    /**
     * delete indexs
     *
     * @param srcId
     * @param syncList
     * @return
     */
    int delete(@Param("srcId") long srcId, @Param("syncList") List<JobSyncTableEntity> syncList);

}
