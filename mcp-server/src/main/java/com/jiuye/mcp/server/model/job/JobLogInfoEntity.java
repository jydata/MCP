package com.jiuye.mcp.server.model.job;

import java.io.Serializable;

/**
 * job log content entity
 *
 * @author kevin
 * @date 2018-10-23
 */
public class JobLogInfoEntity implements Serializable {

    private static final long serialVersionUID = -3429741647802372650L;

    public JobLogInfoEntity(int fromLineNum, int toLineNum, String logInfo, boolean isEnd) {
        this.fromLineNum = fromLineNum;
        this.toLineNum = toLineNum;
        this.logInfo = logInfo;
        this.isEnd = isEnd;
    }

    private int fromLineNum;
    private int toLineNum;
    private String logInfo;
    private boolean isEnd;

    public int getFromLineNum() {
        return fromLineNum;
    }

    public void setFromLineNum(int fromLineNum) {
        this.fromLineNum = fromLineNum;
    }

    public int getToLineNum() {
        return toLineNum;
    }

    public void setToLineNum(int toLineNum) {
        this.toLineNum = toLineNum;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }
}
