package com.jiuye.mcp.agent.service.meta;


/**
 * @author jepson
 * @Date 2018/9/28 11:12 AM.
 */
public interface IMetaDataColumnService<T> {

    /**
     * 全量加载 mcp库的列信息
     *
     * @param columnEntityList
     * @throws Exception
     */
    void getColumnInfo(T columnEntityList) throws Exception;

    /**
     * 更新某库的某表
     *
     * @param host
     * @param port
     * @param userName
     * @param userPassword
     * @param database
     * @param table
     * @param srcKey
     * @throws Exception
     */
    void updateColumnWithTable(String host, Integer port, String userName, String userPassword, String database, String table, String srcKey) throws Exception;

}


