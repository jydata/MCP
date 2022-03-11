package com.jiuye.mcp.server.model.meta;

import java.io.Serializable;
import java.util.List;

/**
 * @author zg
 */
public class TargetSchemaInfoEntity implements Serializable {

    private static final long serialVersionUID = -2549461642451564234L;

    /**
     * 终端名称
     */
    private String targetName;

    /**
     * 终端对应的schema名称
     */
    private List<MetaTargetSchemaEntity> entities;

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public List<MetaTargetSchemaEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<MetaTargetSchemaEntity> entities) {
        this.entities = entities;
    }


}
