package com.jiuye.mcp.server.controller.job;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.job.log.JobLogFileManager;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.param.enums.JobErrorCode;
import com.jiuye.mcp.param.enums.JobSchedulerEnum;
import com.jiuye.mcp.response.Response;
import com.jiuye.mcp.server.model.job.JobLogInfoEntity;
import com.jiuye.mcp.server.model.job.JobSchedulerLogEntity;
import com.jiuye.mcp.server.model.job.JobSchedulerProjectEntity;
import com.jiuye.mcp.server.service.job.IJobSchedulerLogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * job 日志信息
 *
 * @author zhaopeng
 * @date 2018-12-19
 */
@RestController
@RequestMapping(value = "/joblog", produces = {"application/json;charset=UTF-8"})
public class JobSchedulerLogController {

    private static final Logger logger = LoggerFactory.getLogger(JobSchedulerLogController.class.getName());

    @Autowired
    private IJobSchedulerLogService jobSchedulerLogService;

    /**
     * 通过logId 查询Execution detail数据
     * 通过jobId和handleCode查询log状态信息
     */
    @ApiOperation(value = "通过logId查询Execution detail数据，通过jobId和handleCode查询job实例状态信息")
    @RequestMapping(value = "/query_log", method = RequestMethod.POST)
    public Response<List<JobSchedulerLogEntity>> queryLog(@ApiParam(value = "JobSchedulerProjectEntity", required = true) @RequestBody() JobSchedulerProjectEntity searchInfo){

        if(null == searchInfo) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }
        Response<List<JobSchedulerLogEntity>> response = new Response<>();
        try {
            List<JobSchedulerLogEntity> list = jobSchedulerLogService.queryLog(searchInfo);

            response.setItems(list);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        } catch (Exception e) {
            logger.error("query by logId is exception!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.QUERY_ERROR.getCode(), ApplicationErrorCode.QUERY_ERROR.getMessage());
        }
        return response;
    }

    /**
     * 根据ID查询作业日志信息
     *
     * @return
     */
    @ApiOperation(value = "根据ID查询作业日志信息", notes = "根据ID查询作业日志信息,需要TriggerTime和LogID", httpMethod = "POST")
    @RequestMapping(value = "/loginfo", method = RequestMethod.POST)
    public Response<JobLogInfoEntity> logInfo(@RequestBody(required = true) JobSchedulerProjectEntity searchInfo) {
        if (null == searchInfo) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }
        JobSchedulerLogEntity entity = searchInfo.getEntity();
        Response<JobLogInfoEntity> response = new Response<>();
        try {
            // 生成文件名称
            String fileName = JobLogFileManager.generateFileName(new Date(entity.getTriggerTime().getTime()), entity.getLogId());
            // 读取日志文件
            JobLogInfoEntity logInfo = JobLogFileManager.readLog(fileName, 0);
            // is end
            if (null != logInfo.getLogInfo() && logInfo.getFromLineNum() > logInfo.getToLineNum()){
                List<JobSchedulerLogEntity> list = jobSchedulerLogService.queryLog(searchInfo);
                for (JobSchedulerLogEntity logEntity : list){
                    if (logEntity.getHandleCode() != JobSchedulerEnum.INIT.getCode()){
                        logInfo.setEnd(true);
                    }
                }
            }
            response.setItems(logInfo);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        } catch (Exception e) {
            response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            response.setMessage(JobErrorCode.JOB_LOG_QUERY_EXCEPTION.getMessage());
            logger.error("query trigger loginfo is exception!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
        }

        return response;
    }

    @ApiOperation(value = "根据job调度状态查询调度信息", notes = "根据job调度状态查询调度信息", httpMethod = "GET")
    @RequestMapping(value = "/queryJobSchedulerLog", method = RequestMethod.GET)
    public Response<List<JobSchedulerLogEntity>> queryjobSchedulerLog(@ApiParam(value = "job的状态", required = true) @RequestParam(value = "status") String status) {

        List<JobSchedulerLogEntity> jobDefineEnities = jobSchedulerLogService.queryLogByStatus(status);

        Response<List<JobSchedulerLogEntity>> response = Response.createResponse(jobDefineEnities);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }
}
