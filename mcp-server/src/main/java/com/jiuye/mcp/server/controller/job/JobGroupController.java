package com.jiuye.mcp.server.controller.job;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.response.Response;
import com.jiuye.mcp.server.model.job.JobGroupEntity;
import com.jiuye.mcp.server.service.job.IJobGroupService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * job group
 *
 * @author zhonggang
 * @date 2018-12-19
 */
@RestController
@RequestMapping(value = "/group", produces = {"application/json;charset=UTF-8"})
public class JobGroupController {

    private static final Logger logger = LoggerFactory.getLogger(JobGroupController.class.getName());

    @Autowired
    private IJobGroupService jobGroupService;

    /**
     * 查询指定group
     *
     * @return
     */
    @ApiOperation(value = "查询指定group", notes = "查询指定group", httpMethod = "GET")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response<List<JobGroupEntity>> queryGroup(@ApiParam(value = "项目Id",required = true) @RequestParam(value = "projectId") Long projectId) {
        List<JobGroupEntity> list = new ArrayList<>();

        Response<List<JobGroupEntity>> response = new Response<>();
        try {
            list = jobGroupService.queryGroupInfo(projectId);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            response.setItems(list);
        } catch (Exception e) {
            logger.error("Query group is exception!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.QUERY_ERROR.getCode(), ApplicationErrorCode.QUERY_ERROR.getMessage());
        }

        return response;
    }
}
