package com.jiuye.mcp.server.service.job.impl;

import com.google.common.collect.Lists;
import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.job.executor.QuartzManager;
import com.jiuye.mcp.param.enums.*;
import com.jiuye.mcp.server.dao.job.*;
import com.jiuye.mcp.server.dao.meta.MetaConnLinksMapper;
import com.jiuye.mcp.server.dao.meta.MetaDatarouteMapper;
import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.job.*;
import com.jiuye.mcp.server.model.meta.*;
import com.jiuye.mcp.server.service.job.IJobDefinitionService;
import com.jiuye.mcp.server.service.meta.IDataSourceSyncService;
import com.jiuye.mcp.server.service.meta.IDdlRulesService;
import com.jiuye.mcp.server.util.JDBCManager;
import com.jiuye.mcp.server.util.JdbcUtil;
import com.jiuye.mcp.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaopeng
 * @date 2018-11-28
 */
@Service
public class JobDefinitionServiceImpl implements IJobDefinitionService {

    private static final Logger logger = LoggerFactory.getLogger(JobDefinitionServiceImpl.class);

    @Autowired
    private JobDefinitionMapper jobDefinitionMapper;
    @Autowired
    private JobProjectMapper jobProjectMapper;
    @Autowired
    private JobGroupMapper jobGroupMapper;
    @Autowired
    private MetaDatarouteMapper metaDatarouteMapper;
    @Autowired
    private MetaConnLinksMapper metaConnLinksMapper;
    @Autowired
    private IDdlRulesService ddlRulesService;
    @Autowired
    private JobSchedulerLogMapper jobSchedulerLogMapper;
    @Autowired
    private JobSyncTableMapper jobSyncTableMapper;
    @Autowired
    private JobAlterSqlMapper jobAlterSqlMapper;
    @Autowired
    private IDataSourceSyncService dataSourceSyncService;

    /**
     * 根据路由查询mcp数据库
     *
     * @param routeId
     * @return
     */
    @Override
    public DBTableInfoEntity queryDBMcpTree(long routeId) {
        DBTableInfoEntity dbTableInfoEntity = new DBTableInfoEntity();
        List<MetaMysqlTablesEntity> treeList = metaDatarouteMapper.querySynTreeByRouteId(routeId);

        if (treeList != null && treeList.size() != 0) {
            String sourceId = String.valueOf(treeList.get(0).getSrcId());
            String sourceName = metaConnLinksMapper.queryLinkInfo(treeList.get(0).getSrcId()).getLinkName();

            dbTableInfoEntity.setId(sourceId);
            dbTableInfoEntity.setLabel(sourceName);
            dbTableInfoEntity.setIsleaf(false);

            List<DBTableInfo> dblist = new ArrayList<>();
            List<String> schemaList = new ArrayList<>();
            for (MetaMysqlTablesEntity table : treeList) {
                String tableSchema = table.getTableSchema();
                if (!schemaList.contains(tableSchema)) {
                    schemaList.add(tableSchema);
                }
            }

            for (String schema : schemaList) {
                DBTableInfo dbTableInfo = new DBTableInfo();
                dbTableInfo.setIsleaf(false);
                dbTableInfo.setId(sourceId + "#" + schema);
                dbTableInfo.setLabel(schema);
                List<DBTableInfo> leafList = new ArrayList<>();

                for (MetaMysqlTablesEntity table : treeList) {
                    String tableSchema = table.getTableSchema();
                    String tableName = table.getTableName();

                    DBTableInfo leafInfo = new DBTableInfo();
                    leafInfo.setIsleaf(true);
                    leafInfo.setId(schema + "#" + tableName);
                    leafInfo.setLabel(tableName);

                    if (schema.equals(tableSchema)) {
                        leafList.add(leafInfo);
                    }
                }
                dbTableInfo.setChildren(leafList);
                dblist.add(dbTableInfo);
            }
            dbTableInfoEntity.setChildren(dblist);
        }

        return dbTableInfoEntity;
    }

    @Override
    public List<JobDefineEntity> queryJob(JobDefineEntity entity) {
        return jobDefinitionMapper.queryJob(entity);
    }

    @Override
    public int queryTargetTablesByJob(long routeId, long targetSchemaId) {
        return ddlRulesService.queryTargetTablesByJob(routeId, targetSchemaId);
    }

    /**
     * 新增Job
     *
     * @param entity
     * @return
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public ValidateResult save(JobDefineEntity entity) {
        ValidateResult result = null;
        if (entity.getJobId() == null) {
            // save
            result = saveJob(entity);
        } else {
            // update
            result = updateJob(entity);
        }

        return result;
    }

    /**
     * 新增job
     *
     * @param entity
     * @return
     */
    private ValidateResult saveJob(JobDefineEntity entity) {
        // check job name、 group is exist
        ValidateResult validateResult = checkJobExist(entity);
        if (!validateResult.isFlag()) {
            return validateResult;
        }

        Timestamp currTime = DateUtil.getCurrentTime();
        String jobType = entity.getJobType();
        entity.setEnable(JobEnableEnum.YES.getCode());
        entity.setStatus(JobStatusEnum.INIT.getCode());
        entity.setDeleteFlag(DeleteFlagEnum.NO.getCode());
        entity.setCreateTime(currTime);

        if (jobType.equals(JobTypeEnum.FULL.getCode())) {
            // save job
            int saveNum = jobDefinitionMapper.save(entity);
            if (saveNum <= 0) {
                return new ValidateResult(false, ApplicationErrorCode.CREATE_JOB_ERROR.getMessage());
            }

            // save sync table batch
            validateResult = saveFullBatchSyncTable(entity, currTime);
            if (!validateResult.isFlag()) {
                return validateResult;
            }
        } else if (jobType.equals(JobTypeEnum.METADATA.getCode()) ||
                jobType.equals(JobTypeEnum.INCREMENT.getCode())) {
            String srcDb = entity.getSrcDb();
            String srcTable = entity.getSrcTable();
            // 临时处理，job 不保存srcDb、srcTable信息，保存在同步表中，后续全部保存在同步表中后，从sql控制，然后删除掉现在的处理
            entity.setSrcDb(null);
            entity.setSrcTable(null);
            int saveNum = jobDefinitionMapper.save(entity);
            if (saveNum <= 0) {
                throw new BizException(ApplicationErrorCode.CREATE_JOB_ERROR.getCode(),
                        ApplicationErrorCode.CREATE_JOB_ERROR.getMessage());
            }

            // save sync table batch
            entity.setSrcDb(srcDb);
            entity.setSrcTable(srcTable);
            ValidateResult result = saveBatchSyncTable(entity, currTime);
            if (!result.isFlag()) {
                return result;
            }
        } else {
            // save job
            int saveNum = jobDefinitionMapper.save(entity);
            if (saveNum <= 0) {
                throw new BizException(ApplicationErrorCode.CREATE_JOB_ERROR.getCode(),
                        ApplicationErrorCode.CREATE_JOB_ERROR.getMessage());
            }
        }

        return new ValidateResult(true, null);
    }

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    private ValidateResult updateJob(JobDefineEntity entity) {
        // check job name、 group is exist
        ValidateResult validateResult = checkJobExist(entity);
        if (!validateResult.isFlag()) {
            return validateResult;
        }

        String jobType = entity.getJobType();
        Timestamp currTime = DateUtil.getCurrentTime();
        // update
        if (jobType.equals(JobTypeEnum.FULL.getCode())) {
            jobSyncTableMapper.deleteByJobId(entity.getJobId());

            // save full sync table batch
            validateResult = saveFullBatchSyncTable(entity, currTime);
            if (!validateResult.isFlag()) {
                return validateResult;
            }
        } else if (jobType.equals(JobTypeEnum.METADATA.getCode()) ||
                jobType.equals(JobTypeEnum.INCREMENT.getCode())) {
            jobSyncTableMapper.deleteByJobId(entity.getJobId());

            // save sync table batch
            ValidateResult result = saveBatchSyncTable(entity, currTime);
            if (!result.isFlag()) {
                return result;
            }
        }

        // update job
        entity.setUpdateTime(currTime);
        int updateJob = jobDefinitionMapper.update(entity);
        if (updateJob <= 0) {
            throw new BizException(ApplicationErrorCode.UPDATE_JOB_ERROR.getCode(),
                    ApplicationErrorCode.UPDATE_JOB_ERROR.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 校验job name、group是否存在
     *
     * @param entity
     * @return
     */
    private ValidateResult checkJobExist(JobDefineEntity entity) {
        // jobName唯一性校验
        boolean isExists = checkJobNameExists(entity);
        if (isExists) {
            return new ValidateResult(false, ApplicationErrorCode.JOB_NAME_EXIST_MSG.getMessage());
        }

        //判断是否保存group,没有则保存
        boolean isExist = checkExistsGroupName(entity.getProjectId(), entity.getGroupName());
        if (!isExist) {
            int addNum = addGroup(entity);
            if (addNum <= 0) {
                return new ValidateResult(false, ApplicationErrorCode.CREATE_GROUP_ERROR.getMessage());
            }
        }

        return new ValidateResult(true, null);
    }

    /**
     * 批量保存job sync table
     *
     * @param entity
     * @return
     */
    private ValidateResult saveBatchSyncTable(JobDefineEntity entity, Timestamp currTime) {
        String srcDbs = entity.getSrcDb();
        String srcTables = entity.getSrcTable();
        String tarSchemaName = entity.getSchemaName();
        String operator = entity.getCreateUser();
        Long jobId = entity.getJobId();
        String jobType = entity.getJobType();

        List<JobSyncTableEntity> list = Lists.newArrayList();
        if (StringUtils.isEmpty(srcDbs) && StringUtils.isEmpty(srcTables)) {
            // all
            if (jobType.equals(JobTypeEnum.METADATA.getCode())) {
                list = queryMetadataAllDbTable(entity.getSourceId(), jobId, tarSchemaName, operator, currTime);
            } else if (jobType.equals(JobTypeEnum.INCREMENT.getCode())) {
                list = queryIncrementAllDbTable(entity.getRouteId(), entity.getTargetSchemaId(), jobId, tarSchemaName, operator, currTime);
            }
        } else if (StringUtils.isNotEmpty(srcDbs) && StringUtils.isNotEmpty(srcTables)) {
            // srcTable : db1,t1,t2|db2,t3,t4
            list = splitSyncTableList(srcTables, jobId, tarSchemaName, operator, currTime);
        }

        // delete full sync table
        jobSyncTableMapper.deleteByJobId(jobId);

        // add full sync table
        int addSyncTable = jobSyncTableMapper.saveBatch(list);
        if (addSyncTable != list.size()) {
            throw new BizException(ApplicationErrorCode.CREATE_SYNC_TABLE_ERROR.getCode(),
                    ApplicationErrorCode.CREATE_SYNC_TABLE_ERROR.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 批量保存 full job sync table
     *
     * @param entity
     * @return
     */
    private ValidateResult saveFullBatchSyncTable(JobDefineEntity entity, Timestamp currTime) {
        List<JobSyncTableEntity> syncList = entity.getSyncList();
        if (null == syncList || syncList.size() <= 0) {
            throw new BizException(ApplicationErrorCode.SYNC_TABLE_NULL.getCode(), ApplicationErrorCode.SYNC_TABLE_NULL.getMessage());
        }

        // after saving the job, get jobId
        Long jobId = entity.getJobId();
        String targetSchemaName = entity.getSchemaName();
        syncList.forEach(syncTable -> {
            syncTable.setJobId(jobId);
            syncTable.setSchemaName(targetSchemaName);
            syncTable.setDeleteFlag(DeleteFlagEnum.NO.getCode());
            syncTable.setCreateUser(entity.getCreateUser());
            syncTable.setCreateTime(currTime);
        });

        // add full sync table
        int addSyncTable = jobSyncTableMapper.saveBatch(syncList);
        if (addSyncTable != syncList.size()) {
            throw new BizException(ApplicationErrorCode.CREATE_SYNC_TABLE_ERROR.getCode(),
                    ApplicationErrorCode.CREATE_SYNC_TABLE_ERROR.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 查询元数据所有db、table
     *
     * @param sourceId
     * @param jobId
     * @param tarSchemaName
     * @param operator
     * @param currTime
     * @return
     */
    private List<JobSyncTableEntity> queryMetadataAllDbTable(long sourceId, long jobId, String tarSchemaName, String operator, Timestamp currTime) {
        MetaConnLinksEntity link = metaConnLinksMapper.queryLinkInfo(sourceId);
        if (null == link) {
            throw new BizException(MetadataErrorCode.CONNECTION_NULL_ERROR.getCode(),
                    MetadataErrorCode.CONNECTION_NULL_ERROR.getMessage());
        }

        List<JobSyncTableEntity> list = Lists.newArrayList();
        DBTableInfoEntity dbTableTree = dataSourceSyncService.queryDBTree(link);
        if (null != dbTableTree) {
            List<DBTableInfo> dbTableList = dbTableTree.getChildren();
            dbTableList.forEach((DBTableInfo dbTableInfo) -> {
                String srcDb = dbTableInfo.getLabel();
                dbTableInfo.getChildren().forEach(tableInfo -> {
                    String srcTable = tableInfo.getLabel();

                    JobSyncTableEntity syncTableEntity = new JobSyncTableEntity();
                    syncTableEntity.setJobId(jobId);
                    syncTableEntity.setSchemaName(tarSchemaName);
                    syncTableEntity.setDbName(srcDb);
                    syncTableEntity.setTableName(srcTable);
                    syncTableEntity.setDeleteFlag(DeleteFlagEnum.NO.getCode());
                    syncTableEntity.setCreateUser(operator);
                    syncTableEntity.setCreateTime(currTime);
                    list.add(syncTableEntity);
                });
            });
        }

        return list;
    }

    /**
     * 查询增量所有db、table
     *
     * @param routeId
     * @param targetSchemaId
     * @param jobId
     * @param tarSchemaName
     * @param operator
     * @param currTime
     * @return
     */
    private List<JobSyncTableEntity> queryIncrementAllDbTable(long routeId, long targetSchemaId, long jobId, String tarSchemaName, String operator, Timestamp currTime) {
        List<DBTableInfoEntity> dbTableList = ddlRulesService.queryDbAndTable(routeId, targetSchemaId);

        List<JobSyncTableEntity> list = Lists.newArrayList();
        if (null != dbTableList) {
            dbTableList.forEach(dbTableInfo -> {
                String srcDb = dbTableInfo.getLabel();
                if (srcDb.equals("All")) {
                    return;
                }

                dbTableInfo.getChildren().forEach(tableInfo -> {
                    String srcTable = tableInfo.getLabel();
                    if (srcTable.equals("All")) {
                        return;
                    }

                    String id = tableInfo.getId();
                    String[] ids = id.split("#");

                    JobSyncTableEntity syncTableEntity = new JobSyncTableEntity();
                    syncTableEntity.setJobId(jobId);
                    syncTableEntity.setSchemaName(tarSchemaName);
                    syncTableEntity.setDbName(srcDb);
                    syncTableEntity.setTableName(srcTable);
                    syncTableEntity.setDeleteFlag(DeleteFlagEnum.NO.getCode());
                    syncTableEntity.setCreateUser(operator);
                    syncTableEntity.setCreateTime(currTime);
                    syncTableEntity.setDdlRuleId(Long.valueOf(ids[0]));
                    list.add(syncTableEntity);
                });
            });
        }

        return list;
    }

    /**
     * split db、table
     *
     * @param srcTables
     * @param jobId
     * @param tarSchemaName
     * @param operator
     * @param currTime
     * @return
     */
    private List<JobSyncTableEntity> splitSyncTableList(String srcTables, long jobId, String tarSchemaName, String operator, Timestamp currTime) {
        // srcTable : db1,t1,t2|db2,t3,t4
        String[] srcDbTables = null;
        if (srcTables.contains("|")) {
            srcDbTables = srcTables.split("\\|");
        } else {
            srcDbTables = new String[]{srcTables};
        }

        List<JobSyncTableEntity> list = Lists.newArrayList();
        for (String srcDbTable : srcDbTables) {
            String[] srcDbTableArr = srcDbTable.split(",");
            String srcDb = srcDbTableArr[0];
            for (int i = 1; i < srcDbTableArr.length; i++) {
                String srcTable = srcDbTableArr[i];

                JobSyncTableEntity syncTableEntity = new JobSyncTableEntity();
                syncTableEntity.setJobId(jobId);
                syncTableEntity.setSchemaName(tarSchemaName);
                syncTableEntity.setDbName(srcDb);
                if (srcTable.contains("#")) {
                    // increment : db1,123#t1,124#t2
                    String[] srcTableArr = srcTable.split("#");
                    syncTableEntity.setDdlRuleId(Long.valueOf(srcTableArr[0]));
                    syncTableEntity.setTableName(srcTableArr[1]);
                } else {
                    // db1,t1,t2
                    syncTableEntity.setTableName(srcTable);
                }
                syncTableEntity.setDeleteFlag(DeleteFlagEnum.NO.getCode());
                syncTableEntity.setCreateUser(operator);
                syncTableEntity.setCreateTime(currTime);
                list.add(syncTableEntity);
            }
        }

        return list;
    }

    /**
     * 校验groupName在project中是否存在
     *
     * @return
     */
    private boolean checkExistsGroupName(Long projectId, String groupName) {
        JobGroupEntity groupEntity = new JobGroupEntity();
        groupEntity.setGroupName(groupName);
        groupEntity.setProjectId(projectId);
        int num = jobGroupMapper.checkGroup(groupEntity);
        if (num > 0) {
            return true;
        }
        return false;
    }

    /**
     * 新增group
     *
     * @param entity
     * @return
     */
    private int addGroup(JobDefineEntity entity) {
        JobGroupEntity jobGroup = new JobGroupEntity();
        jobGroup.setGroupName(entity.getGroupName());
        jobGroup.setProjectId(entity.getProjectId());
        jobGroup.setDeleteFlag(DeleteFlagEnum.NO.getCode());
        jobGroup.setCreateUser(entity.getCreateUser());

        int saveNum = jobGroupMapper.save(jobGroup);
        if (saveNum == 1) {
            entity.setGroupId(jobGroup.getGroupId());
        }
        return saveNum;
    }

    /**
     * 作业名称唯一性校验
     *
     * @return
     */
    private boolean checkJobNameExists(JobDefineEntity jobDefineEntity) {
        JobDefineEntity entity = jobDefinitionMapper.queryByName(jobDefineEntity);
        if (null == entity) {
            return false;
        }

        return true;
    }


    /**
     * 查询Bin log信息列表
     *
     * @param routeId
     * @return
     */
    @Override
    public List<BinlogInfoEntity> queryBinlogList(Long routeId) {
        List<BinlogInfoEntity> logList = new ArrayList<>();
        MetaConnLinksEntity entity = metaConnLinksMapper.querySourceLinkByRouteId(routeId);

        String sql = "show master logs";
        Connection conn = null;
        PreparedStatement preStatement = null;
        ResultSet rs = null;
        try {
            conn = JDBCManager.factory(JDBCManager.MYSQL).getConnection(entity, JDBCManager.INFORMATION_SCHEMA);
            preStatement = conn.prepareStatement(sql);
            rs = preStatement.executeQuery();
            while (rs.next()) {
                BinlogInfoEntity binlogInfoEntity = new BinlogInfoEntity();
                binlogInfoEntity.setLog_name(rs.getString(1));
                binlogInfoEntity.setFile_size(rs.getLong(2));
                logList.add(binlogInfoEntity);
            }

            if (null != rs) {
                rs.close();
            }
            if (null != preStatement) {
                preStatement.close();
            }
            conn.close();
        } catch (SQLException e) {
            logger.error("Execute Query SQLException.", e);
            throw new BizException(MetadataErrorCode.EXECUTEQUERY_SQLEXCEPTION.getCode(), e.getMessage());
        }

        return logList;
    }


    /**
     * 执行job
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    @Override
    public ValidateResult triggerJob(Long jobId, String operator) throws Exception {
        JobDefineEntity entity = jobDefinitionMapper.queryById(jobId);
        if (null == entity) {
            return new ValidateResult(false, JobErrorCode.JOB_NOT_EXISTS_ERROR.getMessage());
        }

        try {
            // 线程池trigger job
            JobDefineEntity jobInfo = new JobDefineEntity();
            jobInfo.setJobId(jobId);
            jobInfo.setStatus(JobStatusEnum.READY.getCode());
            jobInfo.setUpdateUser(operator);
            jobDefinitionMapper.update(jobInfo);

            // 元数据并且routeId！=0 调用addJob
            if (entity.getJobType().equals(JobTypeEnum.METADATA.getCode()) && entity.getRouteId() != 0L) {
                QuartzManager.addJob(jobId, entity.getGroupName(), entity.getCron());
            } else {
                // 只执行一次的Job
                QuartzManager.addExecuteOnceJob(jobId, entity.getGroupName(), 0);
            }
        } catch (Exception e) {
            updateStatus(jobId, JobStatusEnum.FAIL.getCode(), operator);
            logger.error("Job execution error.", e);
            return new ValidateResult(false, JobErrorCode.JOB_TRIGGER_EXCEPTION.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 保存、执行Job
     *
     * @param jobDefineEntity
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public ValidateResult triggerAndSaveJob(JobDefineEntity jobDefineEntity) throws Exception {
        ValidateResult result = null;
        if (jobDefineEntity.getJobId() == null) {
            // save
            result = saveJob(jobDefineEntity);
        } else {
            // update
            result = updateJob(jobDefineEntity);
        }

        if (!result.isFlag()) {
            return result;
        }

        // 增量、全量、DDL、元数据(同步页面)都只调一次，元数据(Job页面)定时调度
        String jobType = jobDefineEntity.getJobType();
        if (JobTypeEnum.INCREMENT.getCode().equals(jobType) ||
                JobTypeEnum.FULL.getCode().equals(jobType) ||
                JobTypeEnum.DDL.getCode().equals(jobType) ||
                jobDefineEntity.getRouteId() == 0L) {
            // 线程池trigger job
            QuartzManager.addExecuteOnceJob(jobDefineEntity.getJobId(), jobDefineEntity.getGroupName(), 0);
        } else {
            QuartzManager.addJob(jobDefineEntity.getJobId(), jobDefineEntity.getGroupName(), jobDefineEntity.getCron());
        }

        return result;
    }

    /**
     * 暂停作业
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    @Override
    public ValidateResult pauseJob(Long jobId, String operator) throws Exception {
        JobDefineEntity entity = jobDefinitionMapper.queryById(jobId);
        if (null == entity) {
            return new ValidateResult(false, JobErrorCode.JOB_NOT_EXISTS_ERROR.getMessage());
        }

        ValidateResult result = new ValidateResult(true, null);
        try {
            String jobType = entity.getJobType();
            if (jobType.equals(JobTypeEnum.INCREMENT.getCode())) {
                // pause increment job
                result = QuartzManager.pauseIncrementJob(jobId);
                if (!result.isFlag()) {
                    return result;
                }
            } else {
                // pause job
                boolean pauseFlag = QuartzManager.pauseJob(jobId, entity.getGroupName());
                if (!pauseFlag) {
                    result = new ValidateResult(false, JobErrorCode.JOB_PAUSE_FAIL.getMessage());
                }
            }

            //方法执行成功状态修改为wait
            updateStatus(jobId, JobStatusEnum.WAIT.getCode(), operator);
        } catch (Exception e) {
            logger.error("Job pause error!", e);
            result = new ValidateResult(false, JobErrorCode.JOB_PAUSE_EXCEPTION.getMessage());
        }

        return result;
    }

    /**
     * 恢复作业
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    @Override
    public ValidateResult resumeJob(Long jobId, String operator) throws Exception {
        JobDefineEntity entity = jobDefinitionMapper.queryById(jobId);
        if (null == entity) {
            return new ValidateResult(false, JobErrorCode.JOB_NOT_EXISTS_ERROR.getMessage());
        }

        String jobType = entity.getJobType();
        String groupName = entity.getGroupName();
        try {
            if (jobType.equals(JobTypeEnum.INCREMENT.getCode())) {
                // resume increment job(connect)
                boolean resumeIncFlag = QuartzManager.resumeIncrementJob(jobId);
                if (!resumeIncFlag) {
                    return new ValidateResult(false, JobErrorCode.JOB_RESUME_FAIL.getMessage());
                }
            } else {
                // resume job
                boolean resumeFlag = QuartzManager.resumeJob(jobId, groupName);
                if (!resumeFlag) {
                    return new ValidateResult(false, JobErrorCode.JOB_RESUME_FAIL.getMessage());
                }
            }

            //更新jobProject总次数
            JobProjectEntity jobProjectEntity = jobProjectMapper.queryProjectById(entity.getProjectId());
            jobProjectEntity.setExecuteNum(jobProjectEntity.getExecuteNum() + 1);
            jobProjectMapper.updateProject(jobProjectEntity);

            //方法执行成功状态修改为running
            updateStatus(jobId, JobStatusEnum.RUNNING.getCode(), operator);
        } catch (Exception e) {
            logger.error("Job resume error!", e);
            return new ValidateResult(false, JobErrorCode.JOB_RESUME_EXCEPTION.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 关闭作业实例
     *
     * @param
     * @return
     */
    @Override
    public ValidateResult killJob(Long logId, String operator) throws Exception {
        JobSchedulerLogEntity logInfo = new JobSchedulerLogEntity();
        logInfo.setLogId(logId);
        List<JobSchedulerLogEntity> list = jobSchedulerLogMapper.queryLog(logInfo);
        if (null == list) {
            return new ValidateResult(false, JobErrorCode.JOB_NOT_EXISTS_ERROR.getMessage());
        }

        try {
            for (JobSchedulerLogEntity logEntity : list) {
                if (logEntity.getHandleCode() == JobSchedulerEnum.SUCCESS.getCode() ||
                        logEntity.getHandleCode() == JobSchedulerEnum.FAIL.getCode()) {
                    break;
                }

                Long jobId = logEntity.getJobId();

                if (logEntity.getJobType().equals(JobTypeEnum.INCREMENT.getCode())) {
                    ValidateResult pauseResult = QuartzManager.pauseIncrementJob(jobId);
                    if (!pauseResult.isFlag()) {
                        return pauseResult;
                    }
                } else {
                    boolean killFlag = QuartzManager.removeFullJob(jobId, logId);
                    if (!killFlag) {
                        return new ValidateResult(false, JobErrorCode.JOB_KILL_FAIL.getMessage());
                    }
                }

                // 方法执行成功状态修改为FAIL，未执行成功则不改变
                updateStatus(jobId, JobStatusEnum.FAIL.getCode(), operator);

                // 更新jobProject失败次数
                JobProjectEntity jobProjectEntity = jobProjectMapper.queryProjectById(logEntity.getProjectId());
                jobProjectEntity.setFailNum(jobProjectEntity.getFailNum() + 1);
                jobProjectMapper.updateProject(jobProjectEntity);

                logInfo.setHandleCode(JobSchedulerEnum.FAIL.getCode());
                jobSchedulerLogMapper.updateLog(logInfo);
            }
        } catch (Exception e) {
            logger.error("Kill job is error", e);
            return new ValidateResult(false, JobErrorCode.JOB_KILL_EXCEPTION.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * JobDefinition configure
     * 如果 job状态是ready，不执行操作
     * 如果 job类型是metaData并且routeId！=0 或者 job状态是increment 调用 remove方法
     * 其余job调用kill方法
     * 最后统一将状态修改为init
     */
    @Override
    public DbTableSyncEntity configure(Long jobId, String operator) {
        JobDefineEntity entity = jobDefinitionMapper.queryById(jobId);
        if (null == entity) {
            throw new BizException(JobErrorCode.JOB_NOT_EXISTS_ERROR.getCode(), JobErrorCode.JOB_NOT_EXISTS_ERROR.getMessage());
        }

        // kill job, update status
        long routeId = entity.getRouteId();
        String jobType = entity.getJobType();
        String status = entity.getStatus();
        try {
            if (status.equals(JobStatusEnum.RUNNING.getCode())
                    || status.equals(JobStatusEnum.WAIT.getCode())
                    || status.equals(JobStatusEnum.READY.getCode())) {

                if (jobType.equals(JobTypeEnum.METADATA.getCode()) && routeId != 0) {
                    removeJob(jobId, operator);
                } else {
                    JobSchedulerLogEntity logInfo = new JobSchedulerLogEntity();
                    logInfo.setJobId(jobId);
                    logInfo.setHandleCode(JobSchedulerEnum.RUNNING.getCode());
                    JobSchedulerLogEntity logEntity = jobSchedulerLogMapper.queryLogInfo(logInfo);

                    killJob(logEntity.getLogId(), operator);
                }
            }

            updateStatus(jobId, JobStatusEnum.INIT.getCode(), operator);
        } catch (Exception e) {
            logger.error("Update job error.", e);
            throw new BizException(JobErrorCode.JOB_UPDATE_ERROR.getCode(), JobErrorCode.JOB_UPDATE_ERROR.getMessage());
        }

        // 处理返回结果
        return dealConfig(entity);
    }

    /**
     * 处理config 返回的数据信息
     *
     * @param job
     * @return
     */
    private DbTableSyncEntity dealConfig(JobDefineEntity job) {
        DbTableSyncEntity entity = new DbTableSyncEntity();

        String jobType = job.getJobType();
        if (jobType.equals(JobTypeEnum.METADATA.getCode())) {
            // 元数据同步
            MetaConnLinksEntity connEntity = metaConnLinksMapper.queryLinkInfo(job.getSourceId());
            if (null == connEntity) {
                return null;
            }

            // db table tree
            DBTableInfoEntity dbTableTree = dataSourceSyncService.queryDBTree(connEntity);
            entity.setTree(dbTableTree);

            // select id
            List<String> syncTableList = jobSyncTableMapper.querySyncTableByJobId(job.getJobId(), null, null);
            entity.setCheckList(syncTableList);
        } else if (jobType.equals(JobTypeEnum.INCREMENT.getCode())) {
            // 根据routeId、targetSchemaId查询增量db、table列表
            List<DBTableInfoEntity> dbTableList = ddlRulesService.queryDbAndTable(job.getRouteId(), job.getTargetSchemaId());
            entity.setDbTableList(dbTableList);

            JobSyncTableEntity syncTableEntity = new JobSyncTableEntity();
            syncTableEntity.setJobId(job.getJobId());
            List<JobSyncTableEntity> syncTableList = jobSyncTableMapper.query(syncTableEntity);
            if (null != syncTableEntity && syncTableList.size() > 0) {
                Map<String, List<String>> checkIncreMap = new HashMap<>();
                syncTableList.forEach(syncTable -> {
                    String srcDbName = syncTable.getDbName();
                    String srcTableName = syncTable.getDdlRuleId() + "#" + syncTable.getTableName();

                    if (checkIncreMap.containsKey(srcDbName)) {
                        List<String> tableNameList = checkIncreMap.get(srcDbName);
                        tableNameList.add(srcTableName);
                        checkIncreMap.put(srcDbName, tableNameList);
                    } else {
                        List<String> tableNameList = new ArrayList<>();
                        tableNameList.add(srcTableName);
                        checkIncreMap.put(srcDbName, tableNameList);
                    }
                });

                entity.setCheckIncreList(checkIncreMap);
            }
        } else if (jobType.equals(JobTypeEnum.FULL.getCode())) {
            // db、table、column
            List<DBTableInfoEntity> fullDbTableList = ddlRulesService.queryFullDbTableList(job.getRouteId(), job.getTargetSchemaId());
            entity.setDbTableList(fullDbTableList);

            // select
            JobSyncTableEntity syncTableEntity = new JobSyncTableEntity();
            syncTableEntity.setJobId(job.getJobId());
            List<JobSyncTableEntity> syncTableList = jobSyncTableMapper.query(syncTableEntity);
            if (null != syncTableEntity && syncTableList.size() > 0) {
                entity.setCheckFullList(syncTableList);
            }
        }

        return entity;
    }

    /**
     * 移除作业
     *
     * @param
     * @return
     */
    @Override
    public ValidateResult removeJob(Long jobId, String operator) throws Exception {
        JobDefineEntity entity = jobDefinitionMapper.queryById(jobId);
        if (null == entity) {
            return new ValidateResult(false, JobErrorCode.JOB_NOT_EXISTS_ERROR.getMessage());
        }

        try {
            QuartzManager.removeJob(entity.getJobId(), entity.getGroupName());

            JobSchedulerLogEntity logEntity = new JobSchedulerLogEntity();
            logEntity.setJobId(jobId);
            logEntity.setHandleCode(JobSchedulerEnum.RUNNING.getCode());
            List<JobSchedulerLogEntity> logList = jobSchedulerLogMapper.queryLog(logEntity);
            for (JobSchedulerLogEntity logInfo : logList) {
                logInfo.setHandleCode(JobSchedulerEnum.FAIL.getCode());
                jobSchedulerLogMapper.updateLog(logInfo);
            }

            // 方法执行成功状态修改为init，未执行成功则不改变
            updateStatus(jobId, JobStatusEnum.INIT.getCode(), operator);
        } catch (Exception e) {
            logger.error("Job remove error.", e);
            return new ValidateResult(false, JobErrorCode.JOB_KILL_EXCEPTION.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 公共方法
     * 修改任务的执行状态
     */
    private int updateStatus(Long jobId, String status, String operator) throws Exception {
        JobDefineEntity param = new JobDefineEntity();
        param.setJobId(jobId);
        param.setStatus(status);
        param.setUpdateUser(operator);
        param.setUpdateTime(DateUtil.getCurrentTime());
        int rows = jobDefinitionMapper.update(param);

        return rows;
    }

    /**
     * 根据projectId查询agent
     *
     * @param projectId
     * @return
     */
    @Override
    public String queryAgent(Long projectId) {
        return jobProjectMapper.queryAgent(projectId);
    }

    /**
     * 检查元数据同步环境
     *
     * @param sourceId
     * @return
     */
    @Override
    public boolean checkEnvMetadata(long sourceId) throws Exception {
        Connection conn = null;
        try {
            // 查询连接信息
            MetaConnLinksEntity entity = metaConnLinksMapper.queryLinkBySourceId(sourceId);
            if (null == entity) {
                return false;
            }

            // 获取连接
            conn = JDBCManager.factory(JDBCManager.MYSQL).getConnection(entity, JDBCManager.INFORMATION_SCHEMA);
            // 验证表是否存在
            String sql = "select count(*) as tableCount from `TABLES` where TABLE_SCHEMA = 'information_schema' and TABLE_NAME IN ('COLUMNS','SCHEMATA','STATISTICS','TABLES')";

            PreparedStatement preStatement = conn.prepareStatement(sql);
            ResultSet rs = preStatement.executeQuery();
            while (rs.next()) {
                if (rs.getInt("tableCount") != 4) {
                    return false;
                }
            }

            if (null != rs) {
                rs.close();
            }
            if (null != preStatement) {
                preStatement.close();
            }

            return true;
        } catch (Exception e) {
            logger.error("Test database connection error.", e);
            throw new BizException(MetadataErrorCode.TEST_CONNECTION_ERROR.getCode(), MetadataErrorCode.TEST_CONNECTION_ERROR.getMessage());
        } finally {
            // 连接是否关闭
            JdbcUtil.isClosed(conn);
            // 关闭数据库连接
            JdbcUtil.close(conn);
        }
    }


    /**
     * 增量job中的alter sql 保存
     *
     * @param jobId
     * @param routeId
     * @param targetDbName
     * @param srcDbName
     * @param srcTableName
     * @param srcAlterSql
     * @param targetAlterSql
     * @param createUser
     * @param updateUser
     */
    @Override
    public void qzAlter(Long jobId, Long routeId, String srcDbName, String srcTableName, String targetDbName, String targetTableName, String srcAlterSql, String targetAlterSql, String createUser, String updateUser) throws Exception {
        JobAlterSqlEntity jobAlterSqlEntity = new JobAlterSqlEntity();
        jobAlterSqlEntity.setJobId(jobId);
        jobAlterSqlEntity.setRouteId(routeId);

        jobAlterSqlEntity.setSrcSchemaName(srcDbName);
        jobAlterSqlEntity.setSrcTableName(srcTableName);
        jobAlterSqlEntity.setTargetSchemaName(targetDbName);
        jobAlterSqlEntity.setTargetTableName(targetTableName);

        jobAlterSqlEntity.setSrcAlterSql(srcAlterSql);
        jobAlterSqlEntity.setTargetAlterSql(targetAlterSql);
        jobAlterSqlEntity.setCreateUser(createUser);
        jobAlterSqlEntity.setUpdateUser(updateUser);

        jobAlterSqlMapper.save(jobAlterSqlEntity);

    }

}
