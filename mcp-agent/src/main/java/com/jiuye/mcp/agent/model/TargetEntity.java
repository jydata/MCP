package com.jiuye.mcp.agent.model;


import java.io.Serializable;

/**
 * @author jepson
 * @date 2018/9/29 5:53 PM
 */
public class TargetEntity implements Serializable{

    private static final long serialVersionUID = -3338300097263095348L;

    Long targetId;
    String datasourceName;


    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

}
