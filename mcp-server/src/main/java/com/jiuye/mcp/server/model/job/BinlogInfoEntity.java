package com.jiuye.mcp.server.model.job;

import java.io.Serializable;

/**
 * binlog日志信息实体类
 *
 * @author kevin
 * @date 2018-10-26
 */
public class BinlogInfoEntity implements Serializable {

    private static final long serialVersionUID = -6975173352340346418L;

    /**
     * binlog名称
     */
    private String Log_name;
    /**
     * binlog位置
     */
    private Long File_size;

    public String getLog_name() {
        return Log_name;
    }

    public void setLog_name(String log_name) {
        Log_name = log_name;
    }

    public Long getFile_size() {
        return File_size;
    }

    public void setFile_size(Long file_size) {
        File_size = file_size;
    }
}
