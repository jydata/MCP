package com.jiuye.mcp.server.model.job;

import java.io.Serializable;
import java.util.List;

/**
 * @author kevin
 */
public class JobSchemaEntity implements Serializable {

    private static final long serialVersionUID = 3559496304006602265L;

    /**
     * 路由名称
     */
    private String label;
    /**
     * 路由id
     */
    private long id;
    /**
     * 终端schema信息
     */
    private List<JobSchemaEntity> children;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<JobSchemaEntity> getChildren() {
        return children;
    }

    public void setChildren(List<JobSchemaEntity> children) {
        this.children = children;
    }
}
