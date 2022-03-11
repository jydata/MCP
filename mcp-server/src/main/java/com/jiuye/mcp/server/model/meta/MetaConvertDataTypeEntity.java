package com.jiuye.mcp.server.model.meta;


import java.io.Serializable;

/**
 * 源数据转换数据类型实体类
 *
 * @author kevin
 */
public class MetaConvertDataTypeEntity implements Serializable {

    private static final long serialVersionUID = 2297596182197205755L;

    /**
     * 源端类型（mysql，oracle）
     */
    private String sourceType;
    /**
     * 源数据类型
     */
    private String sourceDataType;
    /**
     * 终端类型（phoenix）
     */
    private String targetType;
    /**
     * 终端数据类型
     */
    private String targetDataType;

    public MetaConvertDataTypeEntity() {

    }

    /**
     * 源端类型（mysql，oracle）
     */
    public String getSourceType() {
        return this.sourceType;
    }

    /**
     * 源端类型（mysql，oracle）
     *
     * @param sourceType
     */
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * 源数据类型
     */
    public String getSourceDataType() {
        return this.sourceDataType;
    }

    /**
     * 源数据类型
     *
     * @param sourceDataType
     */
    public void setSourceDataType(String sourceDataType) {
        this.sourceDataType = sourceDataType;
    }

    /**
     * 终端类型（phoenix）
     */
    public String getTargetType() {
        return this.targetType;
    }

    /**
     * 终端类型（phoenix）
     *
     * @param targetType
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    /**
     * 终端数据类型
     */
    public String getTargetDataType() {
        return this.targetDataType;
    }

    /**
     * 终端数据类型
     *
     * @param targetDataType
     */
    public void setTargetDataType(String targetDataType) {
        this.targetDataType = targetDataType;
    }

}
