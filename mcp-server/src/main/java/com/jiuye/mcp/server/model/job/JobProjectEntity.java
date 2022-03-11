package com.jiuye.mcp.server.model.job;

import java.io.Serializable;

/**
 * @author kevin
 */
public class JobProjectEntity implements Serializable {

    private static final long serialVersionUID = -7052252427335063486L;

    private long projectId;
    private String projectName;

    /**
     * 项目描述
     */
    private String projectDescription;

    /**
     * agent存活
     */
    private String projectAgent;

    /**
     * 执行次数
     */
    private int executeNum;

    /**
     * 成功次数
     */
    private int successNum;

    /**
     * 失败次数
     */
    private int failNum;

    /**
     * 删除标记
     */
    private int deleteFlag;
    private String createUser;
    private String createTime;
    private String updateUser;
    private String updateTime;

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getProjectAgent() {
        return projectAgent;
    }

    public void setProjectAgent(String projectAgent) {
        this.projectAgent = projectAgent;
    }

    public int getExecuteNum() {
        return executeNum;
    }

    public void setExecuteNum(int executeNum) {
        this.executeNum = executeNum;
    }

    public int getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    public int getFailNum() {
        return failNum;
    }

    public void setFailNum(int failNum) {
        this.failNum = failNum;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
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
}
