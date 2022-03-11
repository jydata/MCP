package com.jiuye.mcp.server.model.home;

/**
 * @author dove
 * @date 2019-07-02
 */
public class HomeSyncAgentErrorLogEntity {
    private static final long serialVersionUID = 532419961500615569L;

    private String agentName;
    private int todayError;
    private int lastdayError;
    private int lastweekError;

    public HomeSyncAgentErrorLogEntity() {
    }

    public HomeSyncAgentErrorLogEntity(String agentName, int todayError, int lastdayError, int lastweekError) {
        this.agentName = agentName;
        this.todayError = todayError;
        this.lastdayError = lastdayError;
        this.lastweekError = lastweekError;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public int getTodayError() {
        return todayError;
    }

    public void setTodayError(int todayError) {
        this.todayError = todayError;
    }

    public int getLastdayError() {
        return lastdayError;
    }

    public void setLastdayError(int lastdayError) {
        this.lastdayError = lastdayError;
    }

    public int getLastweekError() {
        return lastweekError;
    }

    public void setLastweekError(int lastweekError) {
        this.lastweekError = lastweekError;
    }
}
