package com.jiuye.mcp.server.model.meta;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 数据端信息实体
 *
 * @author kim
 * @date 2018-09-25
 */
public class DictEntity implements Serializable {

    private static final long serialVersionUID = -4946285733858186715L;

    private long dictId;
    /**
     * 数据端名称
     */
    private String dictName;
    /**
     * 数据端标识
     */
    private String databaseFlag;
    /**
     * 数据端类型：0-源端，1-终端
     */
    private int databaseType;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 修改时间
     */
    private Timestamp updateTime;
    /**
     * 数据端类型1：DB Link，2：DS Rule',
     */
    private int dictFlag;
    /**
     * 备注信息
     */
    private String comment;

    private String flag;

    public long getDictId() {
        return dictId;
    }

    public void setDictId(long dictId) {
        this.dictId = dictId;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDatabaseFlag() {
        return databaseFlag;
    }

    public void setDatabaseFlag(String databaseFlag) {
        this.databaseFlag = databaseFlag;
    }

    public int getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(int databaseType) {
        this.databaseType = databaseType;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public int getDictFlag() {
        return dictFlag;
    }

    public void setDictFlag(int dictFlag) {
        this.dictFlag = dictFlag;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
