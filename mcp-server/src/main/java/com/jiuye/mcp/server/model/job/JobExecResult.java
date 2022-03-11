package com.jiuye.mcp.server.model.job;

import java.io.Serializable;

/**
 * 作业执行结果
 *
 * @author kevin
 */
public class JobExecResult implements Serializable {

    private static final long serialVersionUID = -310807589679003790L;

    private boolean isSuccess;
    private String log;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
