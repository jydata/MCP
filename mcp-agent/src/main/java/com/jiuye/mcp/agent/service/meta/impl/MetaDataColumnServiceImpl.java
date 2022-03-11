package com.jiuye.mcp.agent.service.meta.impl;

import com.jiuye.mcp.agent.model.MySQLSourceColumnEntity;
import com.jiuye.mcp.agent.service.meta.IMetaDataColumnService;
import com.jiuye.mcp.agent.util.DecryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 列名称 map  所有列    pos,columnName
 * 列主键 map  主键PRI列 pos,columnName
 *
 * @author jepson
 * @date 29/08/2018 3:51 PM
 */
@Service
public class MetaDataColumnServiceImpl implements IMetaDataColumnService<List<MySQLSourceColumnEntity>> {

    private final static Logger logger = LoggerFactory.getLogger(MetaDataColumnServiceImpl.class.getName());

    // db table cloumn map
    private volatile ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> dbTableColName = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> dbTableColPK = new ConcurrentHashMap<>();

    /**
     * 所有db和table的列信息
     * Map:
     * db.table pos columnname
     * db.table pos pk
     *
     * @param mySQLColumnEntityList
     */
    @Override
    public void getColumnInfo(List<MySQLSourceColumnEntity> mySQLColumnEntityList) {
        ConcurrentHashMap<Integer, String> colName;
        ConcurrentHashMap<Integer, String> colPK;
        String key;
        for (MySQLSourceColumnEntity ce : mySQLColumnEntityList) {
            key = ce.getSrcDBTable();

            if (!dbTableColName.containsKey(key)) {
                colName = new ConcurrentHashMap<Integer, String>();
            } else {
                colName = dbTableColName.get(key);
            }
            colName.put(ce.getColumnPos(), ce.getColumnName());
            dbTableColName.put(key, colName);

            if ("PRI".equals(ce.getColumnKey())) {
                if (!dbTableColPK.containsKey(key)) {
                    colPK = new ConcurrentHashMap<Integer, String>();
                } else {
                    colPK = dbTableColPK.get(key);
                }

                colPK.put(ce.getColumnPos(), ce.getColumnName());
                dbTableColPK.put(key, colPK);
            }
        }
    }


    /**
     * 通过路由ID找到源端 查询指定的db.table
     *
     * @param host
     * @param port
     * @param userName
     * @param userPassword
     * @param database
     * @param table
     */
    @Override
    public void updateColumnWithTable(String host, Integer port, String userName, String userPassword, String database, String table, String srcKey) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        ConcurrentHashMap<Integer, String> colName = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, String> colPK = new ConcurrentHashMap<>();

        try {
            // 注册 JDBC 驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 打开链接
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/", userName, DecryptUtil.getInstance().decrypt(userPassword));
            // 执行查询
            stmt = conn.createStatement();

            String sql = "SELECT\n" +
                    " concat_ws('.',table_schema,table_name) as db_table," +
                    " ordinal_position -1 as column_pos,column_name,column_key" +
                    " FROM" +
                    " INFORMATION_SCHEMA.COLUMNS" +
                    " WHERE TABLE_SCHEMA ='" + database + "'   and TABLE_NAME='" + table + "'";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                colName.put(rs.getInt(2), rs.getString(3));
                if (rs.getString(4).equals("PRI")) {
                    // 主键/联合组件
                    colPK.put(rs.getInt(2), rs.getString(3));
                }
            }

            // update colName colPK info
            if (colName.size() > 0) {
                dbTableColName.put(srcKey, colName);
            }
            if (colPK.size() > 0) {
                dbTableColPK.put(srcKey, colPK);
            }
            // TODO...是否更新mysql元数据表？？？
        } catch (SQLException se) {
            // 处理 JDBC 错误
            logger.error("Udate column meta info error.", se);
        } catch (Exception e) {
            // 处理 Class.forName 错误
            logger.error("Load mysql driver error.", e);
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                logger.error("Close result set error.", e);
            }

            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                logger.error("Close statement error.", e);
            }

            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.error("Close connection error.", e);
            }
        }

    }

    public ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> getDbTableColName() {
        return dbTableColName;
    }

    public void setDbTableColName(ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> dbTableColName) {
        this.dbTableColName = dbTableColName;
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> getDbTableColPK() {
        return dbTableColPK;
    }

    public void setDbTableColPK(ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> dbTableColPK) {
        this.dbTableColPK = dbTableColPK;
    }


}
