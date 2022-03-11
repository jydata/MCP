package com.jiuye.mcp;


import com.jiuye.mcp.netty.server.MCPNettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 启动
 *
 * @author all
 */
@SpringBootApplication
public class MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(MCPServer.class.getName());

    public static void main(String[] args) throws Exception {

        // 启动嵌入式的 Tomcat 并初始化 Spring 环境及其各 Spring 组件
        ApplicationContext context = SpringApplication.run(MCPServer.class, args);
        logger.info("started!");
        MCPNettyServer mcpNettyServer = context.getBean(MCPNettyServer.class);
        mcpNettyServer.run();
    }

    /**
     * @return 下面两个配置为跨域访问的配置
     */
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

}
