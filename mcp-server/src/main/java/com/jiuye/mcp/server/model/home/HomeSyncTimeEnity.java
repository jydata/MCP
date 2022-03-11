package com.jiuye.mcp.server.model.home;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-12-27
 */
public class HomeSyncTimeEnity implements Serializable {

    private static final long serialVersionUID = 532419961500615569L;

    /**
     * sync data分粒度的时间
     */
    private String time;
    /**
     * time下的路由信息
     */
    private List<HomeSyncRouteEntity> routeList;

    /**
     * time下的job信息
     */
    private List<HomeSyncJobEntity> jobList;

    /**
     * time下agent信息
     */
    private List<HomeSyncAgentEntity> agentList;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<HomeSyncRouteEntity> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<HomeSyncRouteEntity> routeList) {
        this.routeList = routeList;
    }

    public List<HomeSyncJobEntity> getJobList() {
        return jobList;
    }

    public void setJobList(List<HomeSyncJobEntity> jobList) {
        this.jobList = jobList;
    }

    public List<HomeSyncAgentEntity> getAgentList() {
        return agentList;
    }

    public void setAgentList(List<HomeSyncAgentEntity> agentList) {
        this.agentList = agentList;
    }
}
