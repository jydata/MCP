package com.jiuye.mcp.server.controller.meta;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.param.enums.MetadataErrorCode;
import com.jiuye.mcp.response.Response;
import com.jiuye.mcp.server.controller.BaseController;
import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.meta.DictEntity;
import com.jiuye.mcp.server.model.meta.MetaConnLinksEntity;
import com.jiuye.mcp.server.service.meta.IMetaConnLinksService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * 元数据管理
 * @author zg
 */
@RestController
@RequestMapping(value = "/conn", produces = {"application/json;charset=UTF-8"})
public class ConnsController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ConnsController.class.getName());
    @Autowired
    private IMetaConnLinksService connsService;



    /**
     * 查询已配置的数据库连接列表
     * 源端、终端数据库列表
     *
     * @param  {pageSize, currentPage}
     * @return
     */

    @ApiOperation(value = "查询数据库")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response<List<MetaConnLinksEntity>> queryConnections(@ApiParam(value = "源/终端区分") @RequestParam(value = "dataSourceType", required = false) Integer dataSourceType) {
        if (null == dataSourceType) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        MetaConnLinksEntity entity = new MetaConnLinksEntity();
        if( null != dataSourceType){
            entity.setDatasourceType(dataSourceType);
        }

        List<MetaConnLinksEntity> list = connsService.queryDBLinkPage(entity);

        Response<List<MetaConnLinksEntity>> response = Response.createResponse(list);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        return response;
    }

    /**
     * 测试数据库配置连接
     * @param param
     * @return
     */
    @ApiOperation(value = "测试数据库配置连接", notes = "测试数据库配置连接", httpMethod = "POST")
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public Response<Boolean> testConnection(@ApiParam(value = "测试数据库", required = true) @RequestBody(required = true) MetaConnLinksEntity param) {
        Response<Boolean> response = new Response<>();
        try {
            ValidateResult validateResult = connsService.testConnection(param);
            if (validateResult.isFlag()){
                response.setItems(true);
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }else {
                response.setItems(false);
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            }
        } catch (TimeoutException ex) {
            logger.error("Connection timeout!", ex);
            throw new BizException(MetadataErrorCode.CONNECTION_TIMEOUT_ERROR.getCode(), MetadataErrorCode.CONNECTION_TIMEOUT_ERROR.getMessage());
        }catch (Exception e) {
            logger.error("Test connection is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(MetadataErrorCode.TEST_CONNECTION_ERROR.getCode(), MetadataErrorCode.TEST_CONNECTION_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 添加数据库连接
     *
     * @param entity
     * @return
     */
    @ApiOperation(value = "添加数据库连接",notes = "添加数据库连接",httpMethod = "POST")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Response<MetaConnLinksEntity> addConnection(HttpServletRequest request, @ApiParam(value = "添加数据库连接", required = true) @RequestBody(required = true) MetaConnLinksEntity entity) {
        if (null == entity) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<MetaConnLinksEntity> response = new Response<>();

        try {
            connsService.addConnection(entity, getUser(request));
            response.setItems(entity);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        } catch (DuplicateKeyException ex) {
            logger.error("Add connection has an error!", ex);
            throw new InvalidRequestException(ApplicationErrorCode.SAME_DATA_ERROR.getCode(), ApplicationErrorCode.SAME_DATA_ERROR.getMessage());
        } catch (Exception e) {
            logger.error("Add connection has an error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 加载数据源列表
     *
     * @param entity
     * @return
     */
    @ApiOperation(value = "加载数据源列表")
    @RequestMapping(value = "/load_options", method = RequestMethod.POST)
    public Response<List<DictEntity>> loadOptions(@ApiParam(value ="加载数据源列表",required = true)@RequestBody(required = true) DictEntity entity) {

        List<DictEntity> list = connsService.loadOptions(entity);

        Response<List<DictEntity>> response = Response.createResponse(list);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

}
