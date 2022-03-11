package com.jiuye.mcp.server.service.job;

import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.job.BinlogInfoEntity;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.meta.DBTableInfoEntity;
import com.jiuye.mcp.server.model.meta.DbTableSyncEntity;

import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-11-28
 */
public interface IJobDefinitionService {

    /**
     * 根据路由查询mcp数据库
     * @param routeId
     * @return
     */
    DBTableInfoEntity queryDBMcpTree(long routeId);

    /**
     * 查询作业对应的源端、终端信息
     *
     * @param entity
     * @return
     */
    List<JobDefineEntity> queryJob(JobDefineEntity entity);

    /**
     * 查询binlog日志列表
     *
     * @param routeId
     * @return
     */
    List<BinlogInfoEntity> queryBinlogList(Long routeId);

    /**
     * 新增Job
     * @param entity
     * @return
     */
    ValidateResult save(JobDefineEntity entity);

    /**
     * 执行作业
     *
     * @param jobId
     * @param operator
     * @return
     * @throws Exception
     */
    ValidateResult triggerJob(Long jobId, String operator) throws Exception;

    /**
     * 执行作业
     * @param jobDefineEntity
     * @return
     * @throws Exception
     */
    ValidateResult triggerAndSaveJob(JobDefineEntity jobDefineEntity) throws Exception;

    /**
     * 暂停作业
     *
     * @param jobId
     * @param operator
     * @return
     * @throws Exception
     */
    ValidateResult pauseJob(Long jobId, String operator) throws Exception;

    /**
     * 恢复作业
     *
     * @param jobId
     * @param operator
     * @return
     * @throws Exception
     */
    ValidateResult resumeJob(Long jobId, String operator) throws Exception;

    /**
     * 关闭作业实例
     *
     * @param LogId
     * @param operator
     * @return
     * @throws Exception
     */
    ValidateResult killJob(Long LogId, String operator) throws Exception;

    /**
     * 移除作业
     *
     * @param jobId
     * @param operator
     * @return
     * @throws Exception
     */
    ValidateResult removeJob(Long jobId, String operator) throws Exception;

    /**
     * configure修改状态,并移除作业
     *
     * @param jobId
     * @param operator
     * @return
     * @throws Exception
     */
    DbTableSyncEntity configure(Long jobId, String operator) throws Exception;

    /**
     * 根据projectId查询agent
     *
     * @param projectId
     * @return
     */
    String queryAgent(Long projectId);

    /**
     * 检查元数据同步环境
     *
     * @param sourceId
     * @return
     * @throws Exception
     */
    boolean checkEnvMetadata(long sourceId) throws Exception;

    /**
     * 判断路由对应的target_schema是否存在同步的表
     * @param routeId
     * @param targetSchemaId
     * @return
     */
    int queryTargetTablesByJob(long routeId, long targetSchemaId);

    /**
     * job的alter sql保存
     *
     * @param jobId
     * @param routeId
     * @param srcDbName
     * @param srcTableName
     * @param targetDbName
     * @param targetTableName
     * @param srcAlterSql
     * @param targetAlterSql
     * @param createUser
     * @param updateUser
     * @throws Exception
     */
    void qzAlter(Long jobId,Long routeId,String srcDbName,String srcTableName,String targetDbName,String targetTableName,String srcAlterSql,String targetAlterSql,String createUser,String updateUser) throws Exception;
}
