package com.jiuye.mcp.server.controller.meta;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.response.Response;
import com.jiuye.mcp.server.model.meta.DBTableInfoEntity;
import com.jiuye.mcp.server.service.meta.IDdlRulesService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author kevin
 */
@RestController
@RequestMapping(value = "/ddl_rule", produces = {"application/json;charset=UTF-8"})
public class MetaDdlRulesController {

    private static final Logger logger = Logger.getLogger(MetaDdlRulesController.class.getName());

    @Autowired
    private IDdlRulesService ddlRulesService;

    /**
     * 根据参数查询数据库、表信息-增量
     *
     * @param routeId
     * @return
     */
    @ApiOperation(value = "根据路由查询数据库、表列表", notes = "根据路由查询数据库、表列表", httpMethod = "GET")
    @RequestMapping(value = "/query_db_table", method = RequestMethod.GET)
    public Response<List<DBTableInfoEntity>> queryDbTable(@ApiParam(value = "路由id", required = true) @RequestParam(value = "routeId") long routeId,
                                                          @ApiParam(value = "终端schemaId", required = true) @RequestParam(value = "targetSchemaId") long targetSchemaId) {
        Response<List<DBTableInfoEntity>> response = new Response<>();

        try {
            List<DBTableInfoEntity> list = ddlRulesService.queryDbAndTable(routeId, targetSchemaId);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            response.setItems(list);
        } catch (Exception e) {
            logger.error("Query db table is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.QUERY_ERROR.getCode(), ApplicationErrorCode.QUERY_ERROR.getMessage());
        }

        return response;
    }


    /**
     * 根据参数查询数据库、表信息-全量
     *
     * @param routeId
     * @return
     */
    @ApiOperation(value = "根据路由查询数据库、表列表", notes = "根据路由查询数据库、表列表", httpMethod = "GET")
    @RequestMapping(value = "/query_full_db_table", method = RequestMethod.GET)
    public Response<List<DBTableInfoEntity>> queryFullDbTableList(@ApiParam(value = "路由id", required = true) @RequestParam(value = "routeId") long routeId,
                                                          @ApiParam(value = "终端schemaId", required = true) @RequestParam(value = "targetSchemaId") long targetSchemaId) {
        Response<List<DBTableInfoEntity>> response = new Response<>();

        try {
            List<DBTableInfoEntity> list = ddlRulesService.queryFullDbTableList(routeId, targetSchemaId);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            response.setItems(list);
        } catch (Exception e) {
            logger.error("Query db table is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.QUERY_ERROR.getCode(), ApplicationErrorCode.QUERY_ERROR.getMessage());
        }

        return response;
    }
}
