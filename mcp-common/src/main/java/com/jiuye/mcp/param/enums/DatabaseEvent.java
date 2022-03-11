package com.jiuye.mcp.param.enums;

/**
 * @author jepson
 */
public enum DatabaseEvent {

    /**
     * 增
     */
    INSERT_ROWS,
    /**
     * 改
     */
    UPDATE_ROWS,
    /**
     * 删
     */
    DELETE_ROWS,
    /**
     * 阻塞，向下分发时使用
     */
    BLOCKING_ROWS;

}
