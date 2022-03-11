package com.jiuye.mcp.server.util.impl;

import com.jiuye.mcp.datasource.PhoenixDataSourceV2;
import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.param.enums.MetadataErrorCode;
import com.jiuye.mcp.server.model.meta.MetaConnLinksEntity;
import com.jiuye.mcp.server.util.IJDBCConnection;
import com.jiuye.mcp.utils.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.*;

/**
 * Phoenix JDBC连接数据库实现类
 *
 * @author zhaopeng
 * @date 2018-12-13
 */
public class PhoenixJDBCConnectionImpl implements IJDBCConnection {

    private static final Logger logger = LoggerFactory.getLogger(PhoenixJDBCConnectionImpl.class.getName());
    private final static int CONN_TIMEOUT = 60;

/*
    @Override
    public Connection getConnection(MetaConnLinksEntity dbLink, String dbName) {
        StringBuffer strBuffer = new StringBuffer();
        Connection conn = null;

        strBuffer.append("jdbc:phoenix:")
                .append(dbLink.getIp())
                .append(":")
                .append(dbLink.getPort())
                .append(":/")
                .append(dbName);
        dbLink.setDriver("org.apache.phoenix.jdbc.PhoenixDriver");
        dbLink.setUrl(strBuffer.toString());

        try {
            Class.forName(dbLink.getDriver());
            Properties properties = new Properties();
            properties.setProperty("phoenix.schema.mapSystemTablesToNamespace", "true");
            properties.setProperty("phoenix.schema.isNamespaceMappingEnabled", "true");
            //properties.setProperty("hbase.rpc.timeout","2000");
            properties.setProperty("hbase.rpc.timeout","5000");
            properties.setProperty("hbase.client.retries.number","1");
            //properties.setProperty("rpc.socket.timeout","5000");
            //properties.setProperty("hbase.client.pause","100");
            properties.setProperty("zookeeper.recovery.retry","1");
            conn = DriverManager.getConnection(strBuffer.toString(), properties);
        } catch (SQLTimeoutException te) {
            logger.error("Connection timeout!", te);
            throw new BizException(MetadataErrorCode.CONNECTION_TIMEOUT_ERROR.getCode(), MetadataErrorCode.CONNECTION_TIMEOUT_ERROR.getMessage());
        } catch (SQLException se) {
            logger.error("Invalid Connection!", se);
            throw new BizException(MetadataErrorCode.TEST_CONNECTION_ERROR.getCode(), MetadataErrorCode.TEST_CONNECTION_ERROR.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return conn;
    }
    */

    @Override
    public Connection getConnection(MetaConnLinksEntity dbLink, String dbName) {
        StringBuffer strBuffer = new StringBuffer();
        Connection conn = null;

        strBuffer.append("jdbc:phoenix:")
                .append(dbLink.getIp())
                .append(":")
                .append(dbLink.getPort())
                .append(":/")
                .append(dbName);
        dbLink.setDriver("org.apache.phoenix.jdbc.PhoenixDriver");
        dbLink.setUrl(strBuffer.toString());

        try {
            //使用线程池的方式来控制数据库连接超时
            final ExecutorService exec = Executors.newFixedThreadPool(1);
            Callable<Connection> call = new Callable<Connection>() {
                public Connection call() throws Exception {
                    SpringBeanFactory.getBean(PhoenixDataSourceV2.class).enableKerberos();
                    return DriverManager.getConnection(dbLink.getUrl());
                }
            };
            Future<Connection> future = exec.submit(call);
            // 如果在设定超时(以秒为单位)之内,还没得到 Connection 对象,则认为连接超时,不继续阻塞
            conn = future.get(1000 * CONN_TIMEOUT, TimeUnit.MILLISECONDS);
            exec.shutdownNow();
        } catch (InterruptedException te) {
            logger.error("Connection timeout!", te);
            throw new BizException(MetadataErrorCode.CONNECTION_TIMEOUT_ERROR.getCode(), MetadataErrorCode.CONNECTION_TIMEOUT_ERROR.getMessage());
        } catch (ExecutionException exe) {
            logger.error("Get connection error", exe);
            throw new BizException(MetadataErrorCode.GET_CONNECTION_ERROR.getCode(), MetadataErrorCode.GET_CONNECTION_ERROR.getMessage());
        } catch (TimeoutException ex) {
            logger.error("Connection timeout!", ex);
            throw new BizException(MetadataErrorCode.CONNECTION_TIMEOUT_ERROR.getCode(), MetadataErrorCode.CONNECTION_TIMEOUT_ERROR.getMessage());
        }

        return conn;
    }

    /**
     * 数据库连接是否关闭
     *
     * @param conn
     */
    @Override
    public void isClosed(Connection conn) throws BizException {
        try {
            if (conn.isClosed()) {
                throw new BizException(MetadataErrorCode.DB_INVALID_ARGUMENTS.getCode(), MetadataErrorCode.DB_INVALID_ARGUMENTS.getMessage());
            }
        } catch (SQLException se) {
            logger.error("Access db is error!", se);
            throw new BizException(MetadataErrorCode.ACCESS_ERROR.getCode(), MetadataErrorCode.ACCESS_ERROR.getMessage());
        }
    }

    /**
     * 关闭数据库连接
     *
     * @param conn
     */
    @Override
    public void close(Connection conn) throws BizException {
        try {
            if (null != conn) {
                conn.close();
            }
        } catch (SQLException se) {
            logger.error("Connection close is error!", se);
            throw new BizException(MetadataErrorCode.CONNECTION_CLOSE_ERROR.getCode(), MetadataErrorCode.CONNECTION_CLOSE_ERROR.getMessage());
        }
    }
}
