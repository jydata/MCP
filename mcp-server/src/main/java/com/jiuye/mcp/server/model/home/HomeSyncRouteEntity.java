package com.jiuye.mcp.server.model.home;

import java.io.Serializable;

/**
 * @author zhaopeng
 * @date 2018-12-27
 */
public class HomeSyncRouteEntity implements Serializable {

    private static final long serialVersionUID = 532419961500615569L;

    /**
     * 路由Id
     */
    private long routeId;
    /**
     * 路由名称
     */
    private String routeName;
    /**
     * 路由对应的record总和
     */
    private long recordSum;

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public long getRecordSum() {
        return recordSum;
    }

    public void setRecordSum(long recordSum) {
        this.recordSum = recordSum;
    }
}
