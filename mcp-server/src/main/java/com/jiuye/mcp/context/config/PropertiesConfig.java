package com.jiuye.mcp.context.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 读取mcp配置文件
 *
 * @author kevin
 * @date 2018-10-24
 */

@Component
public class PropertiesConfig {

    @Value("${mcp.server.job.logPath:/usr/local/dubbo/logs/mcp-job/app}")
    private String logPath;
    @Value("${mcp.job.scheduler.retryCount:0}")
    private String retryCount;

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public String getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(String retryCount) {
        this.retryCount = retryCount;
    }
}
