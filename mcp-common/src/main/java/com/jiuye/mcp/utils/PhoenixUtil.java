package com.jiuye.mcp.utils;

import com.jiuye.mcp.datasource.PhoenixDataSourceV2;
import org.apache.phoenix.jdbc.PhoenixConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Statement;


/**
 * @author jepson
 * @date 2018/9/14 1:39 PM
 */

@Component
public class PhoenixUtil {

    private final static Logger logger = LoggerFactory.getLogger(PhoenixUtil.class.getName());

    @Autowired
    private PhoenixDataSourceV2 phoenixDataSource;
    private PhoenixConnection conn;
    private Statement stmt;

    public void upsertDeleteAlterSql(String sql) throws Exception{
        try {
            conn = (PhoenixConnection)phoenixDataSource.phoenixJdbcConn();
            stmt = conn.createStatement();
            stmt.execute(sql);
            conn.commit();

            logger.info(" Alter ParserSQL Success: " + sql);
        } catch (SQLException ex) {
            //记录sql
            if (ex.getSQLState().equals("42892")) { //列已存在
                logger.warn("\nParserSQL:\n" + sql + "\nParserError:\n", ex);
            } else {
                logger.error("\nParserSQL:\n" + sql + "\nParserError:\n", ex);
            }
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
            }
        }
    }

}
