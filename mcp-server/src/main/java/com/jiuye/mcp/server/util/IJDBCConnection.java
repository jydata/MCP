package com.jiuye.mcp.server.util;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.server.model.meta.MetaConnLinksEntity;

import java.sql.Connection;

/**
 * JDBC连接数据库接口
 * @author zhaopeng
 * @date 2018-12-13
 */
public interface IJDBCConnection {

    /**
     * 获取数据库连接
     *
     * @param dbLink
     * @param dbName
     * @return
     */
    Connection getConnection(MetaConnLinksEntity dbLink, String dbName);

    /**
     * 数据库连接是否关闭
     *
     * @param conn
     * @throws BizException
     */
    void isClosed(Connection conn) throws BizException;

    /**
     * 关闭数据库连接
     *
     * @param conn
     * @throws BizException
     */
    void close(Connection conn) throws BizException;

}
