package com.jiuye.mcp.server.controller.meta;


import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.response.Response;
import com.jiuye.mcp.server.controller.BaseController;
import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.meta.*;
import com.jiuye.mcp.server.service.job.IJobAlterSqlService;
import com.jiuye.mcp.server.service.meta.IDdlService;
import com.jiuye.mcp.server.service.meta.IMetaRulesService;
import com.jiuye.mcp.server.service.meta.IMetaTargetSchemaService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

/**
 * 元数据管理
 *
 * @author zg
 * @date 2018-11-14
 */
@RestController
@RequestMapping(value = "/ddl", produces = {"application/json;charset=UTF-8"})
public class DDLController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DDLController.class.getName());

    @Autowired
    private IDdlService ddlService;
    @Autowired
    private IMetaRulesService metaRulesService;
    @Autowired
    private IJobAlterSqlService jobAlterSqlService;
    @Autowired
    private IMetaTargetSchemaService metaTargetSchemaService;

    /**
     * 查询所有规则名称
     *
     * @return
     */
    @ApiOperation(value = "查询所有规则名称")
    @RequestMapping(value = "/query_rules", method = RequestMethod.GET)
    public Response<List<String>> queryRules() {

        List<String> rulesList = metaRulesService.queryNameList();

        Response<List<String>> response = Response.createResponse(rulesList);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 根据路由名称查询数据库
     *
     * @param targetId
     * @return
     */
    @ApiOperation(value = "根据路由名称查询数据库")
    @RequestMapping(value = "/query_routes", method = RequestMethod.GET)
    public Response<List<MetaDatarouteEntity>> queryRoutes(@ApiParam(value = "终端Id", required = true) @RequestParam(value = "targetId") Long targetId,
                                                           @ApiParam(value = "终端Schema Id", required = true) @RequestParam(value = "targetSchemaId") Long targetSchemaId) {

        List<MetaDatarouteEntity> routeList = ddlService.queryRouteInfo(targetId, targetSchemaId);

        Response<List<MetaDatarouteEntity>> response = Response.createResponse(routeList);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 查询数据库列表以及各自下的表
     *
     * @param targetId
     * @return
     */
    @ApiOperation(value = "查询数据库列表以及各自下的表")
    @RequestMapping(value = "/query_db_trees", method = RequestMethod.GET)
    public Response<List<DBTableInfoEntity>> queryDBTrees(@ApiParam(value = "终端Id", required = true) @RequestParam(value = "targetId") Long targetId) {
        Response<List<DBTableInfoEntity>> response = new Response<>();
        try {
            List<DBTableInfoEntity> dbtableList = ddlService.queryDBTableList(targetId);
            response.setItems(dbtableList);
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            if (dbtableList == null || dbtableList.size() <= 0) {
                response.setMessage("The source information corresponding to the terminal has not yet synchronized the data !");
            }
        } catch (Exception e) {
            logger.error("Query meta db tree is exception!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.QUERY_ERROR.getCode(), ApplicationErrorCode.SAME_DATA_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 根据对应信息查询元数据Metadata
     *
     * @param sourceId
     * @param schema
     * @param table
     * @return
     */
    @ApiOperation(value = "根据对应信息查询元数据Metadata")
    @RequestMapping(value = "/query_table_meta", method = RequestMethod.GET)
    public Response<List<DBTableEntity>> queryTableMeta(@ApiParam(value = "源数据库Id名称", required = true) @RequestParam(value = "sourceId") Long sourceId,
                                                        @ApiParam(value = "数据库名称", required = true) @RequestParam(value = "schema") String schema,
                                                        @ApiParam(value = "表名", required = true) @RequestParam(value = "table") String table) {

        List<DBTableEntity> tableInfoEntity = ddlService.queryTableMeta(sourceId, schema, table);

        Response<List<DBTableEntity>> response = Response.createResponse(tableInfoEntity);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 获取表下的所有Schema信息
     *
     * @returnquery_target_info
     */
    @ApiOperation(value = "获取表下的所有Schema信息")
    @RequestMapping(value = "/query_columns", method = RequestMethod.GET)
    public Response<List<MetaMysqlColumnsEntity>> queryColumns(@ApiParam(value = "表名", required = true) @RequestParam(value = "param") String param) {
        List<MetaMysqlColumnsEntity> schemalist = ddlService.querySchemaInfo(param);

        Response<List<MetaMysqlColumnsEntity>> response = Response.createResponse(schemalist);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 单表同步target SQL 建表
     *
     * @return
     */
    @ApiOperation(value = "单表同步Target SQL 建表")
    @RequestMapping(value = "/exec_sql/{srcId}/{ruleName}/{srcDb}/{routeId}/{targetSchemaId}", method = RequestMethod.POST)
    public Response<ValidateResult> execSQL(@ApiParam(value = "SQL语句", required = true) @RequestBody() List<MetaTargetDdlEntity> param,
                                            @ApiParam(value = "源端Id", required = true) @PathVariable(value = "srcId") long srcId,
                                            @ApiParam(value = "规则名称", required = true) @PathVariable(value = "ruleName") String ruleName,
                                            @ApiParam(value = "源端库名", required = true) @PathVariable(value = "srcDb") String srcDb,
                                            @ApiParam(value = "路由id", required = true) @PathVariable(value = "routeId") long routeId,
                                            @ApiParam(value = "终端schemaId", required = true) @PathVariable(value = "targetSchemaId") long targetSchemaId) {
        Response<ValidateResult> response = new Response<>();
        if (null == param || param.size() <= 0) {
            response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            response.setMessage("Missing parameter: [param] is empty！");
            return response;
        }

        try {
            ValidateResult result = null;
            if (param.get(0).getDdlSql().contains("USING iceberg")) {
                result = ddlService.execIcebergSQL(param, srcId, ruleName, srcDb, routeId, targetSchemaId);
            }
            else {
                result = ddlService.execHBaseSQL(param, srcId, ruleName, srcDb, routeId, targetSchemaId);
            }
            response.setItems(result);
            response.setMessage(result.getMessage());
            if (result.isFlag()) {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            }
        } catch (SQLException se) {
            logger.error("exec hbase sql is error!", se);
            throw new InvalidRequestException(ApplicationErrorCode.TARGET_GENERATION_ERROR.getCode(), ApplicationErrorCode.TARGET_GENERATION_ERROR.getMessage());
        } catch (Exception e) {
            logger.error("exec hbase sql is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.TARGET_GENERATION_ERROR.getCode(), ApplicationErrorCode.TARGET_GENERATION_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 单表转换SQL(Phoenix)
     * flag: 0-未勾选需要二级索引的字段， 1-勾选了需要二级索引字段
     *
     * @return
     */
    @ApiOperation(value = "单表转换SQL")
    @RequestMapping(value = "/generate_sql/{flag}/{routeId}/{schemaId}/{id}/{ruleName}/{srcDb}", method = RequestMethod.POST)
    public Response<MetaTargetDdlEntity> generateSQL(@ApiParam(value = "选中字段集合", required = true) @RequestBody() List<MetaMysqlColumnsEntity> param,
                                                     @ApiParam(value = "索引字段", required = true) @PathVariable(value = "flag") String flag,
                                                     @ApiParam(value = "路由Id", required = true) @PathVariable(value = "routeId") long routeId,
                                                     @ApiParam(value = "数据库Id", required = true) @PathVariable(value = "schemaId") long schemaId,
                                                     @ApiParam(value = "id", required = true) @PathVariable(value = "id") long id,
                                                     @ApiParam(value = "规则名称", required = true) @PathVariable(value = "ruleName") String ruleName,
                                                     @ApiParam(value = "源端库名", required = true) @PathVariable(value = "srcDb") String srcDb) {

        Response<MetaTargetDdlEntity> response = new Response<>();
        try {
            MetaTargetDdlEntity sqlInfo = ddlService.genSingleTableSqlInfo(param, flag, routeId, schemaId, id, ruleName, srcDb);
            response.setItems(sqlInfo);
            if (null == sqlInfo) {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage("Transform fail !");
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
                response.setMessage("Transform successfully !");
            }
        } catch (BizException e) {
            response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    /**
     * 单表保存SQL(Phoenix)
     *
     * @return
     */
    @ApiOperation(value = "单表保存SQL")
    @RequestMapping(value = "/save_sql/{srcDb}/{routeId}", method = RequestMethod.POST)
    public Response<MetaTargetDdlEntity> saveSQL(HttpServletRequest request,
                                                 @ApiParam(value = "Table/Index SQL语句", required = true) @RequestBody() MetaTargetDdlEntity param,
                                                 @ApiParam(value = "源端库名", required = true) @PathVariable(value = "srcDb") String srcDb,
                                                 @ApiParam(value = "路由id", required = true) @PathVariable(value = "routeId") long routeId) {

        Response<MetaTargetDdlEntity> response = Response.createResponse(param);
        try {
            MetaTargetDdlEntity sqlInfoEntity = ddlService.saveTargetTableSQL(param, getUser(request), srcDb, routeId);
            response.setItems(sqlInfoEntity);
            if (sqlInfoEntity != null) {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
                response.setMessage("save successfully !");
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage("save fail !");
            }
        } catch (DuplicateKeyException ex) {
            response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            response.setMessage(ApplicationErrorCode.SAME_DATA_ERROR.getMessage());
        } catch (BizException e) {
            response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            response.setMessage(ApplicationErrorCode.JOB_RUNNING.getMessage());
        }

        return response;
    }


    /**
     * 查询DDL信息
     *
     * @param entity
     * @return
     */
    @ApiOperation(value = "查询DDL信息")
    @RequestMapping(value = "/query_ddl_info", method = RequestMethod.POST)
    public Response<MetaTargetDdlEntity> queryDDLInfo(@ApiParam(value = "DDL信息", required = true) @RequestBody() MetaTargetDdlEntity entity) {
        Response<MetaTargetDdlEntity> response = new Response<>();

        MetaTargetDdlEntity ddlInfo = ddlService.queryDDLInfo(entity);
        response.setItems(ddlInfo);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 查询operate DDL信息
     *
     * @param routeId
     * @param srcDbName
     * @param srcTableName
     * @return
     */
    @ApiOperation(value = "查询bin log信息")
    @RequestMapping(value = "/query_binlogddl_info/{routeId}/{srcDbName}/{srcTableName}", method = RequestMethod.GET)
    public Response<String> queryLogDDLInfo(@ApiParam(value = "bin log信息", required = true) @PathVariable(value = "routeId") Long routeId,
                                            @ApiParam(value = "bin log信息", required = true) @PathVariable(value = "srcDbName") String srcDbName,
                                            @ApiParam(value = "bin log信息", required = true) @PathVariable(value = "srcTableName") String srcTableName) {
        Response<String> response = new Response<>();

        String ddlInfo = jobAlterSqlService.queryTargetOperateDDL(routeId, srcDbName, srcTableName);
        response.setItems(ddlInfo);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 查询终端信息
     *
     * @return
     */
    @ApiOperation(value = "查询终端信息")
    @RequestMapping(value = "/query_target_info", method = RequestMethod.GET)
    public Response<List<TargetSchemaInfoEntity>> queryTargetInfo() {
        Response<List<TargetSchemaInfoEntity>> response = new Response<>();

        List<TargetSchemaInfoEntity> list = metaTargetSchemaService.queryList();
        response.setItems(list);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 根据sourceId查询对应的源端db
     *
     * @param sourceId
     * @return
     */
    @ApiOperation(value = "查询源端DB信息")
    @RequestMapping(value = "/query_source_db", method = RequestMethod.GET)
    public Response<List<String>> querySourceDb(@ApiParam(value = "源端id", required = true) @RequestParam(value = "sourceId") long sourceId) {
        Response<List<String>> response = new Response<>();

        List<String> list = ddlService.querySourceDb(sourceId);
        response.setItems(list);
        if (null == list || list.size() <= 0) {
            response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
        } else {
            response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        }

        return response;
    }

}
