package com.jiuye.mcp.server.model.home;

import java.io.Serializable;

/**
 * @author dove at 2019-06-13
 */
public class HomeTableCountEntity implements Serializable {
    private static final long serialVersionUID = 532419961500615569L;

    /***
     * 业务线
     */
    private String projectName;
    private String sourceDb;
    private String sourceTb;
    private int sourceCount;
    private String targetDb;
    private String targetTb;
    private int targetCount;
    private int diffCount;
    private String timeArea;

    public HomeTableCountEntity(String projectName, String sourceDb, String sourceTb, int sourceCount, String targetDb, String targetTb, int targetCount, int diffCount, String timeArea) {
        this.projectName = projectName;
        this.sourceDb = sourceDb;
        this.sourceTb = sourceTb;
        this.sourceCount = sourceCount;
        this.targetDb = targetDb;
        this.targetTb = targetTb;
        this.targetCount = targetCount;
        this.diffCount = diffCount;
        this.timeArea = timeArea;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSourceDb() {
        return sourceDb;
    }

    public void setSourceDb(String sourceDb) {
        this.sourceDb = sourceDb;
    }

    public String getSourceTb() {
        return sourceTb;
    }

    public void setSourceTb(String sourceTb) {
        this.sourceTb = sourceTb;
    }

    public int getSourceCount() {
        return sourceCount;
    }

    public void setSourceCount(int sourceCount) {
        this.sourceCount = sourceCount;
    }

    public String getTargetDb() {
        return targetDb;
    }

    public void setTargetDb(String targetDb) {
        this.targetDb = targetDb;
    }

    public String getTargetTb() {
        return targetTb;
    }

    public void setTargetTb(String targetTb) {
        this.targetTb = targetTb;
    }

    public int getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(int targetCount) {
        this.targetCount = targetCount;
    }

    public int getDiffCount() {
        return diffCount;
    }

    public void setDiffCount(int diffCount) {
        this.diffCount = diffCount;
    }

    public String getTimeArea() {
        return timeArea;
    }

    public void setTimeArea(String timeArea) {
        this.timeArea = timeArea;
    }
}
