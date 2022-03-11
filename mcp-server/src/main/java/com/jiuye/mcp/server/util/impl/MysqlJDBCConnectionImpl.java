package com.jiuye.mcp.server.util.impl;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.param.enums.AccountErrorCode;
import com.jiuye.mcp.param.enums.MetadataErrorCode;
import com.jiuye.mcp.server.model.meta.MetaConnLinksEntity;
import com.jiuye.mcp.server.util.DecryptUtil;
import com.jiuye.mcp.server.util.IJDBCConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

/**
 * Mysql JDBC连接数据库实现类
 *
 * @author zhaopeng
 * @date 2018-12-13
 */
public class MysqlJDBCConnectionImpl implements IJDBCConnection {
    private static final Logger logger = LoggerFactory.getLogger(MysqlJDBCConnectionImpl.class.getName());

    @Override
    public Connection getConnection(MetaConnLinksEntity dbLink, String dbName) {
        StringBuffer strBuffer = new StringBuffer();
        Connection conn = null;

        strBuffer.append("jdbc:mysql://")
                .append(dbLink.getIp())
                .append(":")
                .append(dbLink.getPort())
                .append("/")
                .append(dbName)
                .append("?useSSL=false");
        dbLink.setDriver("com.mysql.jdbc.Driver");
        dbLink.setUrl(strBuffer.toString());

        // 解密
        String decryptPwd = DecryptUtil.getInstance().decrypt(dbLink.getPassword());

        try {
            Class.forName(dbLink.getDriver());
            //Properties properties = new Properties();
            //properties.setProperty("connectTimeout", "5000");
            conn = DriverManager.getConnection(strBuffer.toString(), dbLink.getUsername(), decryptPwd);
        } catch (ClassNotFoundException e) {
            logger.error("Load db driver is error!");
            throw new BizException(MetadataErrorCode.LOAD_DRIVER_ERROR.getCode(), MetadataErrorCode.LOAD_DRIVER_ERROR.getMessage());
        } catch (SQLTimeoutException te) {
            logger.error("Connection timeout!", te);
            throw new BizException(MetadataErrorCode.CONNECTION_TIMEOUT_ERROR.getCode(), MetadataErrorCode.CONNECTION_TIMEOUT_ERROR.getMessage());
        } catch (SQLException se) {
            logger.error("Invalid username/password!", se);
            throw new BizException(AccountErrorCode.ACCOUNT_PWD_ERROR.getCode(), AccountErrorCode.ACCOUNT_PWD_ERROR.getMessage());
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

    /*public static Connection getConnection(MetaConnLinksEntity dbLink, String dbName) {
        StringBuffer strBuffer = new StringBuffer();
        Connection conn = null;

        strBuffer.append("jdbc:mysql://")
                .append(dbLink.getIp())
                .append(":")
                .append(dbLink.getPort())
                .append("/")
                .append(dbName);
        dbLink.setDriver("com.mysql.jdbc.Driver");
        dbLink.setUrl(strBuffer.toString());

        // 解密
        String decryptPwd = DecryptUtil.getInstance().decrypt(dbLink.getPassword());

        try {
            Class.forName(dbLink.getDriver());
            //Properties properties = new Properties();
            //properties.setProperty("connectTimeout", "5000");
            conn = DriverManager.getConnection(strBuffer.toString(), dbLink.getUsername(), decryptPwd);
        } catch (ClassNotFoundException e) {
            logger.error("Load db driver is error!");
            throw new BizException(MetadataErrorCode.LOAD_DRIVER_ERROR.getCode(), MetadataErrorCode.LOAD_DRIVER_ERROR.getMessage());
        } catch (SQLTimeoutException te) {
            logger.error("Connection timeout!", te);
            throw new BizException(MetadataErrorCode.CONNECTION_TIMEOUT_ERROR.getCode(), MetadataErrorCode.CONNECTION_TIMEOUT_ERROR.getMessage());
        } catch (SQLException se) {
            logger.error("Invalid username/password!", se);
            throw new BizException(MetadataErrorCode.USERNAME_PWD_INVALID_ERROR.getCode(), MetadataErrorCode.USERNAME_PWD_INVALID_ERROR.getMessage());
        }
        return conn;
    }*/

}
