package com.jiuye.mcp.server.util;

import com.jiuye.mcp.datasource.PhoenixDataSourceV2;
import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.param.enums.AccountErrorCode;
import com.jiuye.mcp.param.enums.MetadataErrorCode;
import com.jiuye.mcp.server.model.meta.MetaConnLinksEntity;
import com.jiuye.mcp.utils.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Properties;

/**
 * JDBC连接工具类
 *
 * @author kevin
 * @date 2018-08-28
 */
public class JdbcUtil {

    private static final Logger logger = LoggerFactory.getLogger(JdbcUtil.class.getName());

    // 在redis中保存元数据表信息的key名称
//    public static String INFORMATION_SCHEMA = "information_schema";
//    public static String METADATA_DB_LINK_KEY = "metadataDBLink";
//    public static String METADATA_DB_KEY = "metadataDB";


    /**
     * 获取数据库连接
     *
     * @param dbLink
     * @param dbName
     * @return
     */
    public static Connection getConnection(MetaConnLinksEntity dbLink, String dbName) throws BizException {
        StringBuffer strBuffer = new StringBuffer();

        Connection conn = null;
        if((null == dbLink.getDatasourceChoice() && dbLink.getDriver().contains("mysql")) || (null != dbLink.getDatasourceChoice() && dbLink.getDatasourceChoice().equals("MySQL"))){
//            conn = getMySQLConn(dbLink, dbName);
        } else if(null == dbLink.getDatasourceChoice() && dbLink.getDriver().contains("phoenix") || null != dbLink.getDatasourceChoice() && dbLink.getDatasourceChoice().equals("Phoenix")){
//            conn = getPhoenixConn(dbLink, dbName);
        }

        return conn;
    }

    /**
     * 获取MySQL conn
     *
     * @param dbLink
     * @param dbName
     * @return
     */
    public static Connection getMySQLConn(MetaConnLinksEntity dbLink, String dbName){
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

            SpringBeanFactory.getBean(PhoenixDataSourceV2.class).enableKerberos();
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

    public static Connection getPhoenixConn(MetaConnLinksEntity dbLink, String dbName){
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
            properties.setProperty("hbase.rpc.timeout","5000");
            properties.setProperty("hbase.client.retries.number","3");
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

    /**
     * 数据库连接是否关闭
     *
     * @param conn
     */
    public static void isClosed(Connection conn) throws BizException {
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
    public static void close(Connection conn) throws BizException {
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
