package com.jiuye.mcp.datasource;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;


/**
 * @author jepson
 * @date 2019/1/25 3:45 PM
 */
@Component
public class PhoenixDataSourceV2 {

    private final static Logger logger = LoggerFactory.getLogger(PhoenixDataSourceV2.class);

    @Value("${hbase.secure.user}")
    String hbaseSecureUser;
    @Value("${hbase.secure.user.keytab}")
    String keyPath;
    @Value("${hbase.secure.krb5}")
    String krb5Path;
    @Value("${phoenix.zookeeper.quorum}")
    String zookeeperQuorum;

    @Value("${spring.profiles.active}")
    String springProfilesActive;

    Connection connection = null;
    Properties properties = null;

    /**
     * @return
     */
    public Configuration enableKerberos() {
        try {
            System.setProperty("java.security.krb5.conf", krb5Path);

            Configuration conf = HBaseConfiguration.create();
            conf.setInt("hbase.rpc.timeout", 60000);
            conf.setInt("hbase.client.scanner.timeout.period", 600000);
            conf.setInt("hbase.client.operation.timeout", 600000);
            conf.set("hadoop.security.authentication", "kerberos");
            conf.set("hbase.security.authentication", "kerberos");
            conf.set("hbase.zookeeper.quorum", zookeeperQuorum);
            conf.set("hbase.zookeeper.property.clientPort", "2181");
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab(hbaseSecureUser, keyPath);

            return conf;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Enable kerberos configuration is error.", ex);
            return null;
        }

    }

    /**
     *
     * @return
     */
    public Connection phoenixJdbcConn() {
        try {
            enableKerberos();

            String zookeeperURL = zookeeperQuorum + ":2181:/hbase";
            String phoenixJdbc = "jdbc:phoenix:" + zookeeperURL;

            properties = new Properties();
            properties.setProperty("hbase.rpc.timeout", "60000");
            properties.setProperty("hbase.client.scanner.timeout.period", "600000");
            // 单次数据操作总时间 不包括scan
            properties.setProperty("hbase.client.operation.timeout", "600000");
            properties.setProperty("dfs.client.socket-timeout", "600000");
            properties.setProperty("phoenix.query.keepAliveMs", "600000");
            properties.setProperty("phoenix.query.timeoutMs", "3600000");
            properties.setProperty("phoenix.schema.isNamespaceMappingEnabled","true");
            properties.setProperty("phoenix.schema.mapSystemTablesToNamespace","true");
            properties.setProperty("zookeeper.session.timeout","900000");

            Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
            connection = DriverManager.getConnection(phoenixJdbc, properties);
            connection.setAutoCommit(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Connection phoenix jdbc is error.", ex);
        }
        return connection;
    }

}
