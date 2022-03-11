package com.jiuye.mcp;

import com.jiuye.mcp.agent.service.sys.impl.SysAgentsServiceImpl;
import com.jiuye.mcp.netty.agent.MCPNettyAgent;
import com.jiuye.mcp.netty.agent.MCPNettyAgentHandler;
import com.jiuye.mcp.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author jepson
 */
@SpringBootApplication
public class MCPAgent implements ApplicationListener {
    private final static Logger logger = LoggerFactory.getLogger(MCPAgent.class);

    @Autowired
    private MCPNettyAgentHandler mcpNettyAgentHandler;

    public static String agentId;
    public static String hostname;

    @Autowired
    private SysAgentsServiceImpl sysAgentsServiceImpl;

    @Value("${mcp.netty.agent.port}")
    private int mcpAgentPort;

    /**
     * 自动获取IP地址后1组为agent id
     *
     * @throws UnknownHostException
     */
    private static void getAgentIdAndHostname() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        agentId = address.getHostAddress();
        hostname = address.getHostName();

        if ("127.0.0.1" != agentId) {
            // 截取最后1段
            agentId = agentId.split("\\.")[3];
        } else {
            // agent退出
            logger.info("MCP Agent getting ip is [127.0.0.1].Please to check code and env.");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            // 获取当前节点的IP，选取后最后1个网段 和 机器名称
            getAgentIdAndHostname();

            // 启动嵌入式的 Tomcat 并初始化 Spring 环境及其各 Spring 组件
            ApplicationContext context = SpringApplication.run(MCPAgent.class, args);
            logger.info("started!");
            MCPNettyAgent mcpNettyAgent = context.getBean(MCPNettyAgent.class);
            mcpNettyAgent.run();
        } catch (Exception e) {
            logger.error("Start agent error.", e);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        try {
            // 应用关闭
            if (event instanceof ContextClosedEvent) {
                if (null != mcpNettyAgentHandler && null != mcpNettyAgentHandler.conn) {
                    mcpNettyAgentHandler.conn.commit();
                    mcpNettyAgentHandler.conn.close();

                    //取消注册
                    sysAgentsServiceImpl.updateAgentWithStopped(
                            Long.valueOf(agentId),
                            mcpAgentPort,
                            DateUtil.getCurrentTime());

                    logger.info("Data is committed and mcp agent is closed.");
                }
            }
        } catch (Exception ex) {
             logger.error("Application release resources error.", ex);
        }
    }

}
