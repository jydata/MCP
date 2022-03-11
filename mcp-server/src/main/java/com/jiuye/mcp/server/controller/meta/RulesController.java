package com.jiuye.mcp.server.controller.meta;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.response.Response;
import com.jiuye.mcp.server.controller.BaseController;
import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.meta.DictEntity;
import com.jiuye.mcp.server.model.meta.MetaRulesEntity;
import com.jiuye.mcp.server.service.meta.IMetaRulesService;
import com.jiuye.mcp.server.service.sys.ISysDictService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-11-16
 */
@RestController
@RequestMapping(value = "/rule",produces = {"application/json;charset=UTF-8"})
public class RulesController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(RouteController.class.getName());

    @Autowired
    private IMetaRulesService rulesService;
    @Autowired
    private ISysDictService sysDictService;

    /**
     * 查询Rules Type
     */
    @ApiOperation(value = "加载Rules Type")
    @RequestMapping(value = "/query_type", method = RequestMethod.GET)
    public Response<List<DictEntity>> queryRulesType(){
        List<DictEntity> types = sysDictService.queryRulesType();

        Response<List<DictEntity>> response = Response.createResponse(types);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 加载Rules列表
     */
    @ApiOperation(value = "加载Rules列表")
    @RequestMapping(value = "/query_list", method = RequestMethod.GET)
    public Response<List<MetaRulesEntity>> queryRules() {

        List<MetaRulesEntity> list = rulesService.queryList();
        Response<List<MetaRulesEntity>> response = Response.createResponse(list);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 查询rules信息
     */
    @ApiOperation(value = "查询rules信息")
    @RequestMapping(value = "/rules_info", method = RequestMethod.GET)
    public Response<MetaRulesEntity> queryByName(@ApiParam(value = "Rules信息",required = true) @RequestParam(value = "rule name", required = true) String ruleName) {

        MetaRulesEntity entity = rulesService.queryByName(ruleName);
        Response<MetaRulesEntity> response = Response.createResponse(entity);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 新增Rules信息
     */
    @ApiOperation(value = "新增Rules信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Response<MetaRulesEntity> save(HttpServletRequest request, @ApiParam(value = "Rules信息",required = true) @RequestBody() MetaRulesEntity rulesInfo) {
        if (null == rulesInfo) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<MetaRulesEntity> response = new Response<>();
        try {
            rulesInfo.setCreateUser(getUser(request));
            ValidateResult validateResult = rulesService.save(rulesInfo);
            if (!validateResult.isFlag()){
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage(validateResult.getMessage());
                return response;
            }

            response.setItems(rulesInfo);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        } catch (DuplicateKeyException ex) {
            logger.error("Add rule is exception!", ex);
            throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
        } catch (Exception e) {
            logger.error("Add rule is exception!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 删除Rules信息
     */
    @ApiOperation(value = "删除Rules")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Response<Boolean> delete(@ApiParam(value = "Rules信息",required = true)  @RequestBody() MetaRulesEntity entity) {
        if (null == entity) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<Boolean> response = new Response<>();
        try {
            boolean flag = rulesService.delete(entity);
            response.setItems(flag);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        } catch (Exception e) {
            logger.error("Delete connection is exception!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.DELETE_ERROR.getCode(), ApplicationErrorCode.DELETE_ERROR.getMessage());
        }
        return response;
    }

}
