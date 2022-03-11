package com.jiuye.mcp.server.model.home;

/**
 * @author dove
 * @date 2019-07-02
 *
 */
public class HomeFinenessStatisticEntity {

    private static final long serialVersionUID = 532419961500615569L;

    private String finenessRate;
    private int normalCount;
    private int warnCount;

    public String getFinenessRate() {
        return finenessRate;
    }

    public void setFinenessRate(String finenessRate) {
        this.finenessRate = finenessRate;
    }

    public int getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(int normalCount) {
        this.normalCount = normalCount;
    }

    public int getWarnCount() {
        return warnCount;
    }

    public void setWarnCount(int warnCount) {
        this.warnCount = warnCount;
    }
}
