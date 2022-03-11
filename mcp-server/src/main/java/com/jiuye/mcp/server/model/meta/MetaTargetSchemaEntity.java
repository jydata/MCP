package com.jiuye.mcp.server.model.meta;

import java.io.Serializable;

/**
 * 终端Schema信息实体
 *
 * @author zp
 * @date 2018-10-11
 */
public class MetaTargetSchemaEntity implements Serializable {

    private static final long serialVersionUID = -4946285733858186715L;

    /**
     * 主键ID
     */
    private long schemaId;
    /**
     * 终端ID
     */
    private long targetId;
    /**
     * 终端名称
     */
    private String targetName;
    /**
     * schema名称
     */
    private String schemaName;
    /**
     * 是否已经在终端库生成标识；1：是，0：否
     */
    private int executeFlag;
    /**
     * 是否删除标识；1：未删除（启用），0：已删除（禁用）
     */
    private String deleteFlag;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 修改人
     */
    private String updateUser;
    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 用于标识操作类型：1-新增 || 2-编辑
     */
    private String flag;

    /**
     * 是否为编辑状态
     */
    private boolean isEdit;

    public long getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(long schemaId) {
        this.schemaId = schemaId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public int getExecuteFlag() {
        return executeFlag;
    }

    public void setExecuteFlag(int executeFlag) {
        this.executeFlag = executeFlag;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }
}
