package com.jiuye.mcp.server.model.meta;

import java.io.Serializable;
import java.util.List;

/**
 * 已添加配置的数据库以及库下各自的表信息实体
 *
 * @author zp
 * @date 2018-08-24
 */
public class DBTableInfoEntity implements Serializable {

    private static final long serialVersionUID = -4946285733858186715L;

    /**
     * 是否叶子
     */
    private boolean isleaf;

    /**
     * 库名
     */
    private String id;

    /**
     * 表名
     */
    private String label;

    private List<DBTableInfo> children;

    public DBTableInfoEntity() {
    }

    public DBTableInfoEntity(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public DBTableInfoEntity(boolean isleaf, String id, String label) {
        this.isleaf = isleaf;
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<DBTableInfo> getChildren() {
        return children;
    }

    public void setChildren(List<DBTableInfo> children) {
        this.children = children;
    }

    public boolean isIsleaf() {
        return isleaf;
    }

    public void setIsleaf(boolean isleaf) {
        this.isleaf = isleaf;
    }
}
