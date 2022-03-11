package com.jiuye.mcp.server.controller.meta;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.response.Response;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.meta.DBTableInfoEntity;
import com.jiuye.mcp.server.model.meta.MetaConnLinksEntity;
import com.jiuye.mcp.server.model.meta.MetaMysqlColumnsEntity;
import com.jiuye.mcp.server.model.meta.MetaMysqlTablesEntity;
import com.jiuye.mcp.server.service.meta.IDataSourceSyncService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 元数据同步
 *
 * @author zhaopeng
 * @date 2018-11-20
 */

@RestController
@RequestMapping(value = "/sync", produces = {"application/json;charset=UTF-8"})
public class DataSourceSyncController {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceSyncController.class.getName());

    @Autowired
    private IDataSourceSyncService sourceDataService;

    /**
     * DataSource Tree数据加载
     */
    @ApiOperation(value = "DS Tree结构")
    @RequestMapping(value = "/query_dstree", method = RequestMethod.POST)
    public Response<DBTableInfoEntity> queryDBs(@ApiParam(value = "数据库Link信息", required = true) @RequestBody(required = true) MetaConnLinksEntity entity) {
        if (null == entity) {
            throw new BizException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        DBTableInfoEntity treeEntity = sourceDataService.queryDBTree(entity);

        Response<DBTableInfoEntity> response = Response.createResponse(treeEntity);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * DS Sync 数据库信息统计
     */
    @ApiOperation(value = "数据库信息统计")
    @RequestMapping(value = "/calc_db_info", method = RequestMethod.POST)
    public Response<LinkedHashMap<String, String>> calcDBData(@ApiParam(value = "数据库Link信息", required = true) @RequestBody() MetaConnLinksEntity entity) {
        LinkedHashMap<String, String> dataMap = sourceDataService.calcDBData(entity);
        Response<LinkedHashMap<String, String>> response = Response.createResponse(dataMap);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 查询数据库Table信息
     */
    @ApiOperation(value = "查询数据库Table信息")
    @RequestMapping(value = "query_table", method = RequestMethod.POST)
    public Response<List<MetaMysqlTablesEntity>> loadTableInfo(@ApiParam(value = "数据库Link信息", required = true) @RequestBody() MetaConnLinksEntity entity,
                                                               @ApiParam(value = "String[] 数据库，table", required = true) @RequestParam String[] dataList) {
        List<MetaMysqlTablesEntity> infoEntity = sourceDataService.loadTableInfo(entity, dataList);
        Response<List<MetaMysqlTablesEntity>> response = Response.createResponse(infoEntity);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 查询表的Column信息
     */
    @ApiOperation(value = "查询Column信息")
    @RequestMapping(value = "query_column", method = RequestMethod.POST)
    public Response<List<MetaMysqlColumnsEntity>> loadColumnInfo(@ApiParam(value = "数据库Link信息", required = true) @RequestBody() MetaConnLinksEntity entity,
                                                                 @ApiParam(value = "String[] 数据库，table", required = true) @RequestParam String[] dataList) {
        List<MetaMysqlColumnsEntity> infoEntity = sourceDataService.loadColumnInfo(entity, dataList);
        Response<List<MetaMysqlColumnsEntity>> response = Response.createResponse(infoEntity);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 查询table的样例信息
     */
    @ApiOperation(value = "查询table样例信息")
    @RequestMapping(value = "query_example", method = RequestMethod.POST)
    public Response<List<LinkedHashMap>> loadExample(@ApiParam(value = "数据库Link信息", required = true) @RequestBody() MetaConnLinksEntity entity,
                                                     @ApiParam(value = "String[] 数据库，table", required = true) @RequestParam String[] dataList) {
        List<LinkedHashMap> list = sourceDataService.loadExample(entity, dataList);
        Response<List<LinkedHashMap>> response = Response.createResponse(list);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }


    /**
     * 查询 DDL
     */
    @ApiOperation(value = "查询DDL信息")
    @RequestMapping(value = "load_ddl", method = RequestMethod.POST)
    public Response<String> loadDDLInfo(@ApiParam(value = "数据库Link信息", required = true) @RequestBody() MetaConnLinksEntity entity,
                                        @ApiParam(value = "String[] 数据库，table", required = true) @RequestParam String[] dataList) {
        String ddlInfo = sourceDataService.loadDDLInfo(entity, dataList);
        Response<String> response = Response.createResponse(ddlInfo);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 获得mysql与mcp的schema同步比例 (0：未同步，1：同步一部分，2：同步所有)
     *
     * @param sourceId
     * @return
     */
    @ApiOperation(value = "同步比列(0：未同步，1：同步一部分，2：同步所有)")
    @RequestMapping(value = "/sync_ratio", method = RequestMethod.POST)
    public Response<Integer> syncRation(@ApiParam(value = "连接id") @RequestParam(required = true) long sourceId) {
        int result = sourceDataService.syncRation(sourceId);

        Response<Integer> response = Response.createResponse(result);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 批量同步源数据信息
     *
     * @param entity
     * @return
     */
    @ApiOperation(value = "批量同步源数据信息")
    @RequestMapping(value = "/batch_sync", method = RequestMethod.POST)
    public Response<Boolean> batchSyncMeta(@ApiParam(value = "job实体", required = true) @RequestBody() JobDefineEntity entity) {
        Response<Boolean> response = new Response<>();
        try {
            sourceDataService.loadMetadataForJob(entity);
            response.setItems(true);
            response.setMessage("Synchronization metadata succeeded");
        } catch (DuplicateKeyException ex) {
            throw new InvalidRequestException(ApplicationErrorCode.SAME_DATA_ERROR.getCode(), ApplicationErrorCode.SAME_DATA_ERROR.getMessage());
        } catch (Exception e) {
            logger.error("Batch Sync Meta is exception!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.SAME_DATA_ERROR.getCode(), ApplicationErrorCode.SAME_DATA_ERROR.getMessage());
        }

        return response;
    }

}




