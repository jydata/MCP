package com.jiuye.mcp.server.controller.job;

import com.google.common.collect.Lists;
import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.netty.util.NettySocketChannelHolder;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.param.enums.JobErrorCode;
import com.jiuye.mcp.param.enums.MetadataErrorCode;
import com.jiuye.mcp.response.Response;
import com.jiuye.mcp.server.controller.BaseController;
import com.jiuye.mcp.server.model.SourceRunnerParam;
import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.job.AgentInfoEntity;
import com.jiuye.mcp.server.model.job.BinlogInfoEntity;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.meta.DBTableInfoEntity;
import com.jiuye.mcp.server.model.meta.DbTableSyncEntity;
import com.jiuye.mcp.server.model.meta.MetaConnLinksEntity;
import com.jiuye.mcp.server.service.job.IJobDefinitionService;
import com.jiuye.mcp.server.service.meta.IDataSourceSyncService;
import com.jiuye.mcp.server.service.meta.IMetaConnLinksService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-11-28
 */

@RestController
@RequestMapping(value = "/job", produces = {"application/json;charset=UTF-8"})
public class JobDefinitionController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(JobDefinitionController.class.getName());

    @Autowired
    private IJobDefinitionService jobDefService;
    @Autowired
    private IDataSourceSyncService dataSourceSyncService;
    @Autowired
    private IMetaConnLinksService connService;

    /**
     * 根据源端查询数据库
     *
     * @param routeId
     * @return
     */
    @ApiOperation(value = "根据路由查询mcp数据库", notes = "根据路由查询mcp数据库", httpMethod = "GET")
    @RequestMapping(value = "/query_tree", method = RequestMethod.GET)
    public Response<DBTableInfoEntity> queryDBMcpTree(@ApiParam(value = "路由id", required = true) @RequestParam(value = "routeId") long routeId) {
        Response<DBTableInfoEntity> response = new Response<>();

        try {
            DBTableInfoEntity dbTableInfoEntity = jobDefService.queryDBMcpTree(routeId);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            response.setItems(dbTableInfoEntity);
        } catch (Exception e) {
            logger.error("Query DBTree is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.QUERY_ERROR.getCode(), ApplicationErrorCode.QUERY_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 根据路由查询数据库
     *
     * @return
     */
    @ApiOperation(value = "根据路由查询数据库", notes = "根据路由查询数据库", httpMethod = "GET")
    @RequestMapping(value = "/query_tree_list", method = RequestMethod.GET)
    public Response<DBTableInfoEntity> queryDBTreeList(@ApiParam(value = "路由Id", required = true) @RequestParam(value = "routeId") Long routeId) {

        Response<DBTableInfoEntity> response = new Response<>();
        try {
            DBTableInfoEntity dbTableInfoEntity = dataSourceSyncService.queryDBTree(routeId);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            response.setItems(dbTableInfoEntity);
        } catch (Exception e) {
            logger.error("Query DBTree list is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.QUERY_ERROR.getCode(), ApplicationErrorCode.QUERY_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 查询Agent存活列表
     *
     * @return
     */
    @ApiOperation(value = "查询Agent存活列表", notes = "查询Agent存活列表", httpMethod = "GET")
    @RequestMapping(value = "/query_agents", method = RequestMethod.GET)
    public Response<List<AgentInfoEntity>> queryAgentList() {
        Response<List<AgentInfoEntity>> response = new Response<>();
        List<AgentInfoEntity> resultList = new ArrayList<>();

        // 获取agent id
        List<String> list = NettySocketChannelHolder.getAgentIds();
        list.forEach(id -> {
            AgentInfoEntity entity = new AgentInfoEntity();
            entity.setAgentId(id);
            resultList.add(entity);
        });

        response.setItems(resultList);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        return response;
    }


    /**
     * 查询binlog信息列表
     *
     * @param routeId
     * @return
     */
    @ApiOperation(value = "查询binlog信息列表", notes = "查询binlog信息列表", httpMethod = "GET")
    @RequestMapping(value = "/query_binlog", method = RequestMethod.GET)
    public Response<List<BinlogInfoEntity>> queryBinlogList(@ApiParam(value = "路由id", required = true) @RequestParam(value = "routeId") Long routeId) {
        Response<List<BinlogInfoEntity>> response = new Response<>();

        List<BinlogInfoEntity> list = jobDefService.queryBinlogList(routeId);

        // 按照文件名称倒序排序
        List<BinlogInfoEntity> resultList = Lists.newArrayList(list);
        Collections.sort(resultList, new Comparator<BinlogInfoEntity>() {
            @Override
            public int compare(BinlogInfoEntity o1, BinlogInfoEntity o2) {
                String logName1 = o1.getLog_name();
                String logName2 = o2.getLog_name();
                return logName2.compareTo(logName1);
            }
        });

        response.setItems(resultList);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        return response;
    }

    /**
     * 查询指定binlog信息
     *
     * @param routeId
     * @param logName
     * @return
     */
    @ApiOperation(value = "查询指定binlog信息", notes = "查询指定binlog信息", httpMethod = "GET")
    @RequestMapping(value = "/query_unique_binlog", method = RequestMethod.GET)
    public Response<BinlogInfoEntity> queryBinlog(@ApiParam(value = "路由id", required = true) @RequestParam(value = "routeId") Long routeId,
                                                  @ApiParam(value = "日志名称", required = true) @RequestParam(value = "logName") String logName) {
        Response<BinlogInfoEntity> response = new Response<>();
        BinlogInfoEntity binlogInfoEntity = new BinlogInfoEntity();

        List<BinlogInfoEntity> list = jobDefService.queryBinlogList(routeId);
        for (BinlogInfoEntity binlogEntity : list) {
            if (binlogEntity.getLog_name().equals(logName)) {
                binlogInfoEntity = binlogEntity;
                response.setItems(binlogInfoEntity);
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }
        }
        if (binlogInfoEntity == null) {
            response.setItems(binlogInfoEntity);
            response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
        }

        return response;
    }

    @ApiOperation(value = "查询作业列表", notes = "查询作业列表", httpMethod = "POST")
    @RequestMapping(value = "/query_jobs", method = RequestMethod.POST)
    public Response<List<JobDefineEntity>> queryJob(@ApiParam(value = "JobDefineEntity", required = false) @RequestBody(required = false) JobDefineEntity entity) {

        Response<List<JobDefineEntity>> response = new Response<>();
        try {
            List<JobDefineEntity> list = jobDefService.queryJob(entity);

            response.setItems(list);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        } catch (Exception e) {
            logger.error("Query jobName list is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.QUERY_ERROR.getCode(), ApplicationErrorCode.QUERY_ERROR.getMessage());
        }
        return response;
    }

    /**
     * 添加job
     *
     * @param jobEntity
     * @return
     */
    @ApiOperation(value = "添加作业", notes = "添加作业", httpMethod = "POST")
    @RequestMapping(value = "/add_job", method = RequestMethod.POST)
    public Response<Boolean> addJob(HttpServletRequest request,
                                    @ApiParam(value = "job实体", required = true) @RequestBody(required = true) JobDefineEntity jobEntity) {
        if (null == jobEntity) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<Boolean> response = new Response<>();
        // 作业名称唯一性校验
        jobEntity.setCreateUser(getUser(request));
        try {
            ValidateResult validateResult = jobDefService.save(jobEntity);
            if (!validateResult.isFlag()) {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage(validateResult.getMessage());
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }

            response.setItems(true);
        } catch (Exception e) {
            response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            response.setMessage(ApplicationErrorCode.CREATE_JOB_ERROR.getMessage());
            response.setItems(false);
            logger.error("Add job is error", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.CREATE_JOB_ERROR.getCode(), ApplicationErrorCode.CREATE_JOB_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 执行Job
     *
     * @param jobId
     * @return
     */
    @ApiOperation(value = "执行作业", notes = "执行作业", httpMethod = "PUT")
    @RequestMapping(value = "/trigger", method = RequestMethod.PUT)
    public Response<Boolean> trigger(HttpServletRequest request, @RequestParam(value = "jobId", required = true) Long jobId) {
        if (null == jobId) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<Boolean> response = new Response<>();
        try {
            ValidateResult result = jobDefService.triggerJob(jobId, getUser(request));
            response.setItems(result.isFlag());
            if (!result.isFlag()) {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage(result.getMessage());
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }
        } catch (Exception e) {
            logger.error("Trigger job is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(JobErrorCode.JOB_TRIGGER_EXCEPTION.getCode(), JobErrorCode.JOB_TRIGGER_EXCEPTION.getMessage());
        }

        return response;
    }

    /**
     * 调度Job
     *
     * @param jobDefineEntity
     * @return
     */
    @ApiOperation(value = "调度作业", notes = "调度作业", httpMethod = "PUT")
    @RequestMapping(value = "/trigger_save", method = RequestMethod.PUT)
    public Response<Boolean> triggerAndSave(HttpServletRequest request,
                                            @ApiParam(value = "作业实体", required = true) @RequestBody() JobDefineEntity jobDefineEntity) {
        if (null == jobDefineEntity) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<Boolean> response = new Response<>();
        try {
            jobDefineEntity.setCreateUser(getUser(request));
            ValidateResult result = jobDefService.triggerAndSaveJob(jobDefineEntity);
            response.setItems(result.isFlag());
            if (!result.isFlag()) {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage(result.getMessage());
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
                response.setMessage(result.getMessage());
            }
        } catch (Exception e) {
            logger.error("Trigger and save job is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(JobErrorCode.JOB_TRIGGER_EXCEPTION.getCode(), JobErrorCode.JOB_TRIGGER_EXCEPTION.getMessage());
        }

        return response;
    }

    /**
     * 暂停作业
     *
     * @param jobId
     * @return
     */
    @ApiOperation(value = "暂停作业", notes = "暂停作业", httpMethod = "PUT")
    @RequestMapping(value = "/pause", method = RequestMethod.PUT)
    public Response<Boolean> pause(HttpServletRequest request, @RequestParam(value = "jobId", required = true) Long jobId) {
        if (null == jobId) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<Boolean> response = new Response<>();
        try {
            ValidateResult result = jobDefService.pauseJob(jobId, getUser(request));
            response.setItems(result.isFlag());
            if (!result.isFlag()) {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage(result.getMessage());
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }
        } catch (Exception e) {
            logger.error("Pause job is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(JobErrorCode.JOB_PAUSE_EXCEPTION.getCode(), JobErrorCode.JOB_PAUSE_EXCEPTION.getMessage());
        }

        return response;
    }

    /**
     * 恢复作业
     *
     * @param jobId
     * @return
     */
    @ApiOperation(value = "恢复作业", notes = "恢复作业", httpMethod = "PUT")
    @RequestMapping(value = "/resume", method = RequestMethod.PUT)
    public Response<Boolean> resume(HttpServletRequest request, @RequestParam(value = "jobId", required = true) Long jobId) {
        if (null == jobId) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<Boolean> response = new Response<>();
        try {
            ValidateResult result = jobDefService.resumeJob(jobId, getUser(request));
            response.setItems(result.isFlag());
            if (!result.isFlag()) {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage(result.getMessage());
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }
        } catch (Exception e) {
            logger.error("Resume job is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(JobErrorCode.JOB_RESUME_EXCEPTION.getCode(), JobErrorCode.JOB_RESUME_EXCEPTION.getMessage());
        }

        return response;
    }

    /**
     * 关闭作业实例
     *
     * @param LogId
     * @return
     */
    @ApiOperation(value = "关闭作业实例", notes = "关闭作业实例", httpMethod = "PUT")
    @RequestMapping(value = "/kill", method = RequestMethod.PUT)
    public Response<Boolean> killJob(HttpServletRequest request, @RequestParam(required = true) Long LogId) {
        if (null == LogId) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<Boolean> response = new Response<>();
        try {
            ValidateResult result = jobDefService.killJob(LogId, getUser(request));
            response.setItems(result.isFlag());
            if (!result.isFlag()) {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage(result.getMessage());
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }
        } catch (Exception e) {
            logger.error("Kill job is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(JobErrorCode.JOB_KILL_EXCEPTION.getCode(), JobErrorCode.JOB_KILL_EXCEPTION.getMessage());
        }

        return response;
    }

    /**
     * 移除作业
     *
     * @param jobId
     * @return
     */
    @ApiOperation(value = "移除作业", notes = "移除作业", httpMethod = "PUT")
    @RequestMapping(value = "/remove", method = RequestMethod.PUT)
    public Response<Boolean> removeJob(HttpServletRequest request, @RequestParam(required = true) Long jobId) {
        if (null == jobId) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<Boolean> response = new Response<>();
        try {
            ValidateResult result = jobDefService.removeJob(jobId, getUser(request));
            response.setItems(result.isFlag());
            if (!result.isFlag()) {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage(result.getMessage());
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }
        } catch (Exception e) {
            logger.error("Remove job is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(JobErrorCode.JOB_KILL_EXCEPTION.getCode(), JobErrorCode.JOB_KILL_EXCEPTION.getMessage());
        }

        return response;
    }

    /**
     * 检查环境
     * 1、校验Agent是否启动
     * 2、校验源端数据信息是否连接成功
     * 3、校验终端数据信息是否连接成功
     *
     * @param entity
     * @return
     */
    @ApiOperation(value = "增量/全量同步-检查环境", notes = "增量/全量同步-检查环境", httpMethod = "POST")
    @RequestMapping(value = "/check_env_increment", method = RequestMethod.POST)
    public Response<ValidateResult> checkEnvIncrement(@ApiParam(value = "环境检查作业实体", required = true) @RequestBody JobDefineEntity entity) {
        if (null == entity) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<ValidateResult> response = new Response<>();

        StringBuffer envMsg = new StringBuffer();
        boolean agentFlag = true;

        // 先校验路由信息
        ValidateResult result = checkRouteEnvironment(entity);
        envMsg.append(result.getMessage());

        List<String> list = NettySocketChannelHolder.getAgentIds();
        String agents = jobDefService.queryAgent(entity.getProjectId());
        if (agents.contains(",")) {
            String[] agentArr = agents.split(",");
            for (String agent : agentArr) {
                if (!list.contains(agent)) {
                    agentFlag = false;
                    envMsg.append("3.Agent：[" + agent + "]Not yet started<br/>");
                }
            }
        } else {
            if (!list.contains(agents)) {
                agentFlag = false;
                envMsg.append("3.Agent：[" + agents + "]Not yet started<br/>");
            }
        }
        // agent成功
        if (agentFlag) {
            envMsg.append("3.Agent has started successfully<br/>");
        }

        // 判断最终结果
        if (agentFlag && result.isFlag()) {
            result.setFlag(true);
        } else {
            result.setFlag(false);
        }
        result.setMessage(envMsg.toString());

        response.setItems(result);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        return response;
    }

    /**
     * 校验路由对应的源端、终端数据信息是否连接成功
     *
     * @param entity
     * @return
     */
    private ValidateResult checkRouteEnvironment(JobDefineEntity entity) {
        StringBuffer envMsg = new StringBuffer();
        ValidateResult result = new ValidateResult();
        boolean srcFlag = true;
        boolean tarFlag = true;
        boolean routeFlag = true;

        // 根据选择的路由，查询对应的源端、终端数据连接信息
        SourceRunnerParam dbParam = null;
        try {
            dbParam = dataSourceSyncService.querySrcAndTarLinkByRouteId(entity);
        } catch (Exception e) {
            envMsg.append("1.The source data information corresponding to the route is not queried.<br/>");
            envMsg.append("2.The source data information corresponding to the route is not queried.<br/>");
            result.setFlag(false);
            result.setMessage(envMsg.toString());

            return result;
        }

        if (null == dbParam) {
            routeFlag = false;
            envMsg.append("1.The source data information corresponding to the route is not queried.<br/>");
            envMsg.append("2.The source data information corresponding to the route is not queried.<br/>");
        } else {
            try {
                MetaConnLinksEntity srcEntity = new MetaConnLinksEntity();
                srcEntity.setDriver(dbParam.getSrcDriver());
                srcEntity.setDbName(dbParam.getSrcDbName());
                srcEntity.setIp(dbParam.getSrcIp());
                srcEntity.setPort(dbParam.getSrcPort());
                srcEntity.setUsername(dbParam.getSrcUsername());
                srcEntity.setPassword(dbParam.getSrcPassword());
                srcEntity.setDatasourceChoice(dbParam.getSrcDatasourceName());
                connService.testConnection(srcEntity);

                envMsg.append("1.The source data information connection succeeded.<br/>");
            } catch (Exception e) {
                srcFlag = false;
                envMsg.append("1.The Source data information connection failed<br/>");
            }

            try {
                MetaConnLinksEntity tarEntity = new MetaConnLinksEntity();
                tarEntity.setDriver(dbParam.getTarDriver());
                tarEntity.setDbName(dbParam.getTarDbName());
                tarEntity.setIp(dbParam.getTarIp());
                tarEntity.setPort(dbParam.getTarPort());
                tarEntity.setUsername(dbParam.getTarUsername());
                tarEntity.setPassword(dbParam.getTarPassword());
                tarEntity.setDatasourceChoice(dbParam.getTarDatasourceName());
                connService.testConnection(tarEntity);

                envMsg.append("2.Terminal data information connection succeeded<br/>");
            } catch (Exception e) {
                tarFlag = false;
                envMsg.append("2.Terminal data information connection failed<br/>");
            }
        }

        // 判断增量、全量对应的target_schema是否存在表
        if (entity.getJobType().equals("increment") || entity.getJobType().equals("full")) {
            int targetTableCount = jobDefService.queryTargetTablesByJob(entity.getRouteId(), entity.getTargetSchemaId());
            if (targetTableCount <= 0) {
                envMsg.append("No table exists in terminal Library!<br/>");
                tarFlag = false;
            }
        }

        // 判断最终结果
        if (srcFlag && tarFlag && routeFlag) {
            result.setFlag(true);
        } else {
            result.setFlag(false);
        }
        result.setMessage(envMsg.toString());

        return result;
    }

    /**
     * 检查同步元数据环境
     *
     * @param sourceId
     * @return
     */
    @ApiOperation(value = "同步元数据-检查环境", notes = "同步元数据-检查环境", httpMethod = "POST")
    @RequestMapping(value = "/check_env_metadata", method = RequestMethod.POST)
    public Response<ValidateResult> checkEnvMetadata(@ApiParam(value = "环境检查源端", required = true) @RequestParam(value = "sourceId") Long sourceId) {
        if (null == sourceId || 0 == sourceId) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<ValidateResult> response = new Response<>();
        ValidateResult validateResult = new ValidateResult();
        try {
            boolean flag = jobDefService.checkEnvMetadata(sourceId);
            if (flag) {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
                validateResult.setMessage(MetadataErrorCode.CHECK_ENV_META_SUCCESS.getMessage());
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                validateResult.setMessage(MetadataErrorCode.CHECK_ENV_META_ERROR.getMessage());
            }
            validateResult.setFlag(flag);
            response.setItems(validateResult);
        } catch (Exception e) {
            logger.error("Check metadata environment is error!", e);
            throw new InvalidRequestException(MetadataErrorCode.CHECK_ENV_META_ERROR.getCode(), MetadataErrorCode.CHECK_ENV_META_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 检查同步DDL环境
     *
     * @param routeId
     * @return
     */
    @ApiOperation(value = "DDL转换-检查环境", notes = "DDL转换-检查环境", httpMethod = "POST")
    @RequestMapping(value = "/check_env_ddl", method = RequestMethod.POST)
    public Response<ValidateResult> checkEnvDDL(@ApiParam(value = "环境检查终端", required = true) @RequestParam(value = "routeId") Long routeId) {
        if (null == routeId || 0L == routeId) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<ValidateResult> response = new Response<>();
        try {
            ValidateResult validateResult = connService.checkEvnDDL(routeId);
            if (validateResult.isFlag()) {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            }
            response.setItems(validateResult);
        } catch (Exception e) {
            logger.error("Check ddl environment is error!", e);
            if (e instanceof BizException) {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage(e.getMessage());
            } else {
                throw new InvalidRequestException(MetadataErrorCode.CHECK_ENV_DDL_ERROR.getCode(), MetadataErrorCode.CHECK_ENV_DDL_ERROR.getMessage());
            }
        }

        return response;
    }

    /**
     * JobDefinition configure
     * 如果 job状态是ready，不执行操作
     * 如果 job类型是metaData并且routeId！=0 或者 job状态是increment 调用 remove方法
     * 其余job调用kill方法
     * 最后统一将状态修改为init
     */
    @ApiOperation(value = "ready状态configure后修改为init", notes = "ready状态configure后修改为init", httpMethod = "PUT")
    @RequestMapping(value = "/configure", method = RequestMethod.PUT)
    public Response<DbTableSyncEntity> configure(HttpServletRequest request, @RequestParam(required = true) Long jobId) {
        if (null == jobId) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<DbTableSyncEntity> response = new Response<>();
        try {
            DbTableSyncEntity result = jobDefService.configure(jobId, getUser(request));
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            response.setItems(result);
        } catch (Exception e) {
            logger.error("Configure job is error!", e);
            if (e instanceof BizException) {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage(e.getMessage());
            } else {
                throw new InvalidRequestException(JobErrorCode.JOB_KILL_EXCEPTION.getCode(), JobErrorCode.JOB_KILL_EXCEPTION.getMessage());
            }
        }

        return response;
    }
}
