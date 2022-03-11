package com.jiuye.mcp.server.controller.meta;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.response.Response;
import com.jiuye.mcp.server.controller.BaseController;
import com.jiuye.mcp.server.model.job.JobSchemaEntity;
import com.jiuye.mcp.server.model.meta.*;
import com.jiuye.mcp.server.service.meta.IDdlService;
import com.jiuye.mcp.server.service.meta.IMetaDatarouteService;
import com.jiuye.mcp.server.service.meta.IMetaTargetSchemaService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-11-14
 */
@RestController
@RequestMapping(value = "/route", produces = {"application/json;charset=UTF-8"})
    public class RouteController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(RouteController.class.getName());

    @Autowired
    private IMetaDatarouteService routeService;
    @Autowired
    private IDdlService ddlService;
    @Autowired
    private IMetaTargetSchemaService metaTargetSchemaService;


    /**
     * 查询路由列表
     */
    @ApiOperation(value = "查询路由列表")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response<List<MetaDatarouteEntity>> queryRoutes(@ApiParam(value = "路由ID") @RequestParam(value = "routeID", required = false) Long routeId,
                                                           @ApiParam(value = "路由名称") @RequestParam(value = "routeName", required = false) String routeName,
                                                           @ApiParam(value = "源端名称") @RequestParam(value = "sourceName", required = false) String sourceName,
                                                           @ApiParam(value = "终端名称") @RequestParam(value = "targetName", required = false) String targetName,
                                                           @ApiParam(value = "路由状态") @RequestParam(value = "routeStatus", required = false) String routeStatus) {

        MetaDatarouteEntity searchEntity = new MetaDatarouteEntity();
        if (null != routeId){
            searchEntity.setRouteId(routeId);
        }
        searchEntity.setRouteName(routeName);
        searchEntity.setSourceName(sourceName);
        searchEntity.setTargetName(targetName);
        searchEntity.setRouteStatus(routeStatus);

        List<MetaDatarouteEntity> list = routeService.queryDBRoutePage(searchEntity);

        Response<List<MetaDatarouteEntity>> response =Response.createResponse(list);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 查询路由
     * @return
     */
    @ApiOperation(value = "查询路由", notes = "查询路由", httpMethod = "GET")
    @RequestMapping(value = "/job_routes", method = RequestMethod.GET)
    public Response<List<JobSchemaEntity>> queryJobRoutes() {
        List<JobSchemaEntity> list = new ArrayList<>();

        Response<List<JobSchemaEntity>> response = new Response<>();
        try {
            list = routeService.queryJobRoutes();
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            response.setItems(list);
        } catch (Exception e) {
            logger.error("Query route is exception!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.QUERY_ERROR.getCode(), ApplicationErrorCode.QUERY_ERROR.getMessage());
        }

        return response;
    }


    /**
     * 新增路由信息
     */
    @ApiOperation(value = "新增路由列表")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Response<MetaDatarouteEntity> saveRoute(HttpServletRequest request, @ApiParam(value = "路由信息",required = true) @RequestBody() MetaDatarouteEntity routeInfo) {
        if (null == routeInfo) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<MetaDatarouteEntity> response = new Response<>();
        try {
            int cnt = routeService.checkRoute(routeInfo);

            if (cnt == 0) {
                routeInfo.setCreateUser(getUser(request));
                routeService.addRoute(routeInfo);
                response.setItems(routeInfo);
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            } else {
                throw new InvalidRequestException(ApplicationErrorCode.SAME_DATA_ERROR.getCode(), ApplicationErrorCode.SAME_DATA_ERROR.getMessage());
            }
        } catch (DuplicateKeyException ex) {
            logger.error("Add Route has an error!", ex);
            throw new InvalidRequestException(ApplicationErrorCode.SAME_DATA_ERROR.getCode(), ApplicationErrorCode.SAME_DATA_ERROR.getMessage());
        } catch (Exception e) {
            logger.error("Add Route has an error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 修改路由状态
     * @param entity
     * @return
     */
    @ApiOperation(value = "修改路由状态")
    @RequestMapping(value = "/update_status", method = RequestMethod.POST)
    public Response<MetaDatarouteEntity> updateRouteStatus(HttpServletRequest request, @ApiParam(value = "路由信息", required = true) @RequestBody() MetaDatarouteEntity entity) {
        if (null == entity) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<MetaDatarouteEntity> response = new Response<>();
        try {
            if (entity.getRouteStatus().equals("1")) {
                int cnt = routeService.checkRoute(entity);

                if (cnt == 0) {
                    routeService.updateRouteStatus(entity, getUser(request));
                    response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
                } else {
                    throw new InvalidRequestException(ApplicationErrorCode.SAME_ROUTE_STATUS_ERROR.getCode(), ApplicationErrorCode.SAME_ROUTE_STATUS_ERROR.getMessage());
                }
            } else {
                routeService.updateRouteStatus(entity, getUser(request));
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }
        } catch (Exception e) {
            logger.error("Update Route Status has an error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.UPDATE_ERROR.getCode(), ApplicationErrorCode.UPDATE_ERROR.getMessage());
        }

        return response;
    }


    /**
     * 修改路由名称
     */
    @ApiOperation(value = "修改路由名称")
    @RequestMapping(value = "/update_name", method = RequestMethod.POST)
    public Response<MetaDatarouteEntity> updateRouteName(HttpServletRequest request, @ApiParam(value = "路由名称", required = true) @RequestBody(required = true) MetaDatarouteEntity entity) {
        if (null == entity) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<MetaDatarouteEntity> response = new Response<>();
        try {
            if (!entity.getRouteName().equals("")) {
                int cnt = routeService.checkRoute(entity);

                if (cnt == 0) {
                    routeService.updateRouteName(entity, getUser(request));
                    response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
                } else {
                    throw new InvalidRequestException(ApplicationErrorCode.SAME_ROUTE_STATUS_ERROR.getCode(), ApplicationErrorCode.SAME_ROUTE_STATUS_ERROR.getMessage());
                }
            } else {
                throw  new InvalidRequestException(ApplicationErrorCode.UPDATE_ERROR.getCode(),ApplicationErrorCode.UPDATE_ERROR.getMessage());
            }
        } catch (Exception e) {
            logger.error("Update Route Status has an error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.UPDATE_ERROR.getCode(), ApplicationErrorCode.UPDATE_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 加载Target Schema信息
     */
    @ApiOperation(value = "加载Target Schema信息")
    @RequestMapping(value = "/query_schema", method = RequestMethod.POST)
    public Response<List<MetaTargetSchemaEntity>> querySchemaLists(@ApiParam(value = "MetaTargetSchemaEntity") @RequestBody(required = false) MetaTargetSchemaEntity entity) {

        List<MetaTargetSchemaEntity> list = metaTargetSchemaService.queryList(entity);

        Response<List<MetaTargetSchemaEntity>> response = Response.createResponse(list);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 新增Schema信息
     * 修改Schema信息和状态
     */
    @ApiOperation(value = "新增 flag='1'、修改Schema信息 flag='2'及状态 falg='3'")
    @RequestMapping(value = "/save_schema", method = RequestMethod.POST)
    public Response<MetaTargetSchemaEntity> saveSchemaInfo(HttpServletRequest request, @ApiParam(value = "Target Schema实体类schama信息", required = true) @RequestBody() List<MetaTargetSchemaEntity> schemaList){
        if (null == schemaList) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<MetaTargetSchemaEntity> response = new Response<>();
        try {
            metaTargetSchemaService.saveSchemaInfo(schemaList, getUser(request));
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        } catch (DuplicateKeyException ex) {
            logger.error("Add Schema has an error!", ex);
            throw new InvalidRequestException(ApplicationErrorCode.SAME_DATA_ERROR.getCode(), ApplicationErrorCode.SAME_DATA_ERROR.getMessage());
        } catch (Exception e) {
            logger.error("Add Schema has an error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
        }

        return response;
    }


    /**
     * 在终端生成Schema信息
     */
    @ApiOperation(value = "在终端生成Schema信息,schemaId是必须参数")
    @RequestMapping(value = "/create_schema", method = RequestMethod.POST)
    public Response<MetaTargetSchemaEntity> createSchemaInfo(HttpServletRequest request, @ApiParam(value = "Schema信息") @RequestBody() List<MetaTargetSchemaEntity> param) {
        if (null == param) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<MetaTargetSchemaEntity> response = new Response<>();
        try {
            metaTargetSchemaService.createSchema(param, getUser(request));
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        } catch (DuplicateKeyException ex) {
            logger.error("Create Schema has an error!", ex);
            throw new InvalidRequestException(ApplicationErrorCode.SAME_DATA_ERROR.getCode(), ApplicationErrorCode.SAME_DATA_ERROR.getMessage());
        } catch (Exception e) {
            logger.error("Create Schema has an error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.CREATE_SCHEMA_ERROR.getCode(), ApplicationErrorCode.CREATE_SCHEMA_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 判断mcp是否存在表
     * @param param
     * @return
     */
    @ApiOperation(value = "判断源端是否存在表")
    @RequestMapping(value = "/exist_table", method = RequestMethod.GET)
    public Response<Boolean> existTables(@ApiParam(value = "srcId集合", required = true) @RequestParam(value = "param") List<Long> param) {
        if (null == param) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<Boolean> response = new Response<>();
        try {
            boolean flag = ddlService.existTables(param);
            if(!flag){
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
                response.setItems(false);
            }else {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
                response.setItems(true);
            }
        } catch (DuplicateKeyException ex) {
            logger.error("Query Tables has an error!", ex);
            throw new InvalidRequestException(ApplicationErrorCode.SAME_DATA_ERROR.getCode(), ApplicationErrorCode.SAME_DATA_ERROR.getMessage());
        } catch (Exception e) {
            logger.error("Query Tables has an error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
        }

        return response;
    }


}
