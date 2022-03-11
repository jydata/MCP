package com.jiuye.mcp.server.model.meta;

import com.jiuye.mcp.server.model.job.JobSyncTableEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 表结构同步信息实体
 *
 * @author zp
 * @date 2018-10-09
 */
public class DbTableSyncEntity implements Serializable {

    private static final long serialVersionUID = -4946285733858186715L;

    /**
     * metadata select
     */
    private List<String> checkList;

    /**
     * increment select
     */
    private Map<String, List<String>> checkIncreList;

    /**
     * full select
     */
    private List<JobSyncTableEntity> checkFullList;

    /**
     * metadata
     * db table tree
     */
    private DBTableInfoEntity tree;

    /**
     * increment、full
     * db table list
     */
    private List<DBTableInfoEntity> dbTableList;

    public List<String> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<String> checkList) {
        this.checkList = checkList;
    }

    public Map<String, List<String>> getCheckIncreList() {
        return checkIncreList;
    }

    public void setCheckIncreList(Map<String, List<String>> checkIncreList) {
        this.checkIncreList = checkIncreList;
    }

    public DBTableInfoEntity getTree() {
        return tree;
    }

    public void setTree(DBTableInfoEntity tree) {
        this.tree = tree;
    }

    public List<DBTableInfoEntity> getDbTableList() {
        return dbTableList;
    }

    public void setDbTableList(List<DBTableInfoEntity> dbTableList) {
        this.dbTableList = dbTableList;
    }

    public List<JobSyncTableEntity> getCheckFullList() {
        return checkFullList;
    }

    public void setCheckFullList(List<JobSyncTableEntity> checkFullList) {
        this.checkFullList = checkFullList;
    }
}
