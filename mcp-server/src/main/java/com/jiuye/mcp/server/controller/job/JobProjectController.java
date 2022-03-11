package com.jiuye.mcp.server.controller.job;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.response.Response;
import com.jiuye.mcp.server.controller.BaseController;
import com.jiuye.mcp.server.model.job.JobProjectEntity;
import com.jiuye.mcp.server.service.job.IJobProjectService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * JobProjectController
 *
 * @author zg
 * @date 2018-11-28
 */
@RestController
@RequestMapping(value = "/project", produces = {"application/json;charset=UTF-8"})
public class JobProjectController  extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(JobProjectController.class.getName());

    @Autowired
    private IJobProjectService jobProjectService;

    /**
     * 查询project
     *
     * @return
     */
    @ApiOperation(value = "查询项目", notes = "查询项目", httpMethod = "GET")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response<List<JobProjectEntity>> queryProject(@ApiParam(value = "项目名称") @RequestParam(value = "projectName",required = false) String projectName) {
        List<JobProjectEntity> list = new ArrayList<>();

        Response<List<JobProjectEntity>> response = new Response<>();
        try {
            list = jobProjectService.queryByName(projectName);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            response.setItems(list);
        } catch (Exception e) {
            logger.error("Query project is exception!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.QUERY_ERROR.getCode(), ApplicationErrorCode.QUERY_ERROR.getMessage());
        }

        return response;
    }

    /**
     *Project Name
     */
    @ApiOperation(value = "加载Project Name")
    @RequestMapping(value = "/query_name", method = RequestMethod.GET)
    public Response<List<JobProjectEntity>> queryProjectName(){

        List<JobProjectEntity> list = jobProjectService.loadProjectName();

        Response<List<JobProjectEntity>> response = Response.createResponse(list);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;

    }

    /**
     * 新增或更新project
     * @param request
     * @param jobProjectEntity
     * @return
     */
    @ApiOperation(value = "新增或更新项目", notes = "新增或更新项目", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<Integer> editProject(HttpServletRequest request,
                                          @ApiParam(value = "project对象", required = true) @RequestBody() JobProjectEntity jobProjectEntity) {
        if (null == jobProjectEntity) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<Integer> response = new Response<>();
        try {
            int result = jobProjectService.editProject(jobProjectEntity, getUser(request));
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            response.setItems(result);
        } catch (Exception e) {
            logger.error("Add or Update project is exception!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 删除project
     *
     * @param projectId
     * @return
     */
    @ApiOperation(value = "删除项目", notes = "删除项目", httpMethod = "DELETE")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Response<Integer> deleteProject(@ApiParam(value = "project对象", required = true) @RequestParam(value = "projectId") long projectId) {
        if (0 == projectId) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<Integer> response = new Response<>();
        try {
            int result = jobProjectService.deleteProject(projectId);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            response.setItems(result);
        } catch (Exception e) {
            logger.error("Delete project is exception!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.UPDATE_ERROR.getCode(), ApplicationErrorCode.UPDATE_ERROR.getMessage());
        }

        return response;
    }

}
