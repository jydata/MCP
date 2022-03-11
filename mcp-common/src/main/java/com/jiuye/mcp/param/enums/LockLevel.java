package com.jiuye.mcp.param.enums;

/**
 * 保持顺序的级别
 * TABLE -> 同表按顺序执行
 * COLUMN -> 某列值一致的按顺序执行
 * NONE -> 无序
 *
 * @author jepson
 */
public enum LockLevel {

    /**
     * 表
     */
    TABLE,
    /**
     * 列
     */
    COLUMN,
    /**
     * 无序
     */
    NONE;

}
