package com.jiuye.mcp.server.service.meta.impl;

import com.jiuye.mcp.param.enums.MetadataErrorCode;
import com.jiuye.mcp.server.dao.meta.MetaConnLinksMapper;
import com.jiuye.mcp.server.dao.meta.MetaDatarouteMapper;
import com.jiuye.mcp.server.dao.sys.SysDictMapper;
import com.jiuye.mcp.server.model.SourceRunnerParam;
import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.meta.DictEntity;
import com.jiuye.mcp.server.model.meta.MetaConnLinksEntity;
import com.jiuye.mcp.server.service.meta.IMetaConnLinksService;
import com.jiuye.mcp.server.util.IJDBCConnection;
import com.jiuye.mcp.server.util.JDBCManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kim
 * @date 2018/11/14
 */
@Service
public class MetaConnLinksServiceImpl implements IMetaConnLinksService {

    private static final Logger logger = LoggerFactory.getLogger(MetaConnLinksServiceImpl.class.getName());

    @Autowired
    private MetaConnLinksMapper metaConnLinksMapper;
    @Autowired
    private SysDictMapper sysDictMapper;
    @Autowired
    private MetaDatarouteMapper metaDatarouteMapper;

    /**
     * 查询所有数据库列表
     *
     * @return
     */
    @Override
    public List<MetaConnLinksEntity> queryDBLinkPage(MetaConnLinksEntity entity) {
        return metaConnLinksMapper.queryLinkList(entity);
    }

    /**
     * 测试连接
     *
     * @param entity
     * @return
     */
    @Override
    public ValidateResult testConnection(MetaConnLinksEntity entity) throws Exception {
        // 获取连接
        IJDBCConnection JDBCImpl = JDBCManager.factory(entity.getDatasourceChoice());
        Connection conn = JDBCImpl.getConnection(entity, entity.getDbName());
        if (null == conn) {
            return new ValidateResult(false, MetadataErrorCode.TEST_CONNECTION_ERROR.getMessage());
        } else {
            JDBCImpl.isClosed(conn);
            JDBCImpl.close(conn);
        }

        return new ValidateResult(true, null);
    }

    /**
     * 添加数据库
     *
     * @param entity
     */
    @Override
    public void addConnection(MetaConnLinksEntity entity, String operator) throws Exception {
        // 获取连接
        IJDBCConnection jdbcImpl = JDBCManager.factory(entity.getDatasourceChoice());
        Connection conn = jdbcImpl.getConnection(entity, entity.getDbName());
        jdbcImpl.isClosed(conn);

        // 拼接连接map的key
        entity.setCreateUser(operator);
        entity.setUpdateUser("");
        entity.setExecuteFlag(0);

        metaConnLinksMapper.saveConn(entity);
        // 关闭数据库连接
        jdbcImpl.close(conn);
    }

    /**
     * 查询所有数据库列表
     * 0：源端数据类型名称
     * 1：终端数据类型名称
     * 2：环境
     * 3:查询数据源下拉框
     *
     * @return
     */
    @Override
    public List<DictEntity> loadOptions(DictEntity entity) {
        List<DictEntity> list = new ArrayList();

        switch (entity.getFlag()) {
            case "3":
            case "0":
                list = sysDictMapper.queryDatabaseName(entity);
                break;
            case "2":
                if (StringUtils.isNotEmpty(entity.getDictName())) {
                    list = sysDictMapper.queryDatabaseName(entity);
                } else {
                    list = null;
                }
                break;
            case "1":
                list = sysDictMapper.queryDistinctNameList(entity);
                break;
        }

        return list;
    }

    /**
     * 检查环境-DDL
     *
     * @param routeId
     * @return
     */
    @Override
    public ValidateResult checkEvnDDL(Long routeId) {
        MetaConnLinksEntity tarLinkEntity = metaConnLinksMapper.queryTargetLinkByRouteId(routeId);
        if (null == tarLinkEntity) {
            return new ValidateResult(false, MetadataErrorCode.CHECK_ENV_DDL_ERROR.getMessage());
        }

        SourceRunnerParam sourceRunnerParam = metaDatarouteMapper.querySrcAndTarLinkByRouteId(routeId);
        tarLinkEntity.setDatasourceChoice(sourceRunnerParam.getTarDatasourceName());
        try {
            testConnection(tarLinkEntity);
        } catch (Exception e) {
            logger.error("Test database connection failed！", e);
            return new ValidateResult(false, MetadataErrorCode.CHECK_ENV_DDL_ERROR.getMessage());
        }

        return new ValidateResult(true, MetadataErrorCode.CHECK_ENV_DDL_SUCCESS.getMessage());
    }

}
