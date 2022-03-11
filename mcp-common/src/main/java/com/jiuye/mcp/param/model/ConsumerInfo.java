package com.jiuye.mcp.param.model;

import com.jiuye.mcp.param.enums.DatabaseEvent;
import com.jiuye.mcp.param.enums.LockLevel;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author jepson
 */
public class ConsumerInfo implements Serializable {

    private static final long serialVersionUID = 6844115179558039773L;

    /**
     * 消费者编号
     */
    private String consumerId;

   
    /**
     * 关注的数据库名
     */
    private String databaseName;

    /**
     * 关注的表名
     */
    private String tableName;

    /**
     * 关注的表的事件
     */
    private DatabaseEvent databaseEvent;

    /**
     * 数据锁定级别
     */
    private LockLevel lockLevel;

    /**
     * 锁级别为列的时候，使用指定列名
     */
    private String columnName;

    /**
     * 拼接key，避免频繁拼接
     */
    private String key;

    public ConsumerInfo() {
    }

    public ConsumerInfo(String consumerId, String databaseName, String tableName, DatabaseEvent databaseEvent, LockLevel lockLevel, String columnName) {
        this.consumerId = consumerId;
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.databaseEvent = databaseEvent;
        this.lockLevel = lockLevel;
        this.columnName = columnName;
        switch (lockLevel) {
            case TABLE:
                key = consumerId + "-" + lockLevel + "-" + databaseName + "-" + tableName;
                break;
            case COLUMN:
                key = consumerId + "-" + lockLevel + "-" + databaseName + "-" + tableName + "-";
                break;
            case NONE:
            default:
                key = consumerId + "-" + lockLevel + "-" + databaseName;
        }
    }

    public LockLevel getLockLevel() {
        return lockLevel;
    }

    public void setLockLevel(LockLevel lockLevel) {
        this.lockLevel = lockLevel;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

  
    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public DatabaseEvent getDatabaseEvent() {
        return databaseEvent;
    }

    public void setDatabaseEvent(DatabaseEvent databaseEvent) {
        this.databaseEvent = databaseEvent;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsumerInfo)) return false;
        ConsumerInfo that = (ConsumerInfo) o;
        return Objects.equals(consumerId, that.consumerId) &&
                Objects.equals(databaseName, that.databaseName) &&
                Objects.equals(tableName, that.tableName) &&
                databaseEvent == that.databaseEvent &&
                lockLevel == that.lockLevel &&
                Objects.equals(columnName, that.columnName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(consumerId, databaseName, tableName, databaseEvent, lockLevel, columnName);
    }

    @Override
    public String toString() {
        return "{" +
                "\"consumerId\":\"" + consumerId + '\"' +
                ", \"databaseName\":\"" + databaseName + '\"' +
                ", \"tableName\":\"" + tableName + '\"' +
                ", \"databaseEvent\":\"" + databaseEvent + '\"' +
                ", \"lockLevel\":\"" + lockLevel + '\"' +
                ", \"columnName\":\"" + columnName + '\"' +
                ", \"key\":\"" + key +
                "\"}";
    }
}
