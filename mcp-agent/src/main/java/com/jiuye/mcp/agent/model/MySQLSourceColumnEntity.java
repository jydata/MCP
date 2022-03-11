package com.jiuye.mcp.agent.model;


import java.io.Serializable;

/**
 * 数据源为MySQL的列名称和主键信息
 *
 * @author jepson
 * @date 2018/9/11 4:08 PM
 */
public class MySQLSourceColumnEntity implements Serializable {

    private static final long serialVersionUID = -8486048797102505929L;

    /**
     * 源端id.database.table
     */
    private String srcDBTable;
    private Integer columnPos;
    private String columnName;
    private String columnKey;

    public String getSrcDBTable() {
        return srcDBTable;
    }

    public void setSrcDBTable(String srcDBTable) {
        this.srcDBTable = srcDBTable;
    }

    public Integer getColumnPos() {
        return columnPos;
    }

    public void setColumnPos(Integer columnPos) {
        this.columnPos = columnPos;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }


}
