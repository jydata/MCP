package com.jiuye.mcp.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author kevin
 */
@Configuration
public class VerifyWebConfigurer extends WebMvcConfigurerAdapter {

    @Bean
    VerifyInterceptor interceptor(){
        return new VerifyInterceptor();
    }

    /**
     * 增加拦截器，追加让拦截器拦截的请求路径
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/mcp/user_management/login");
                //.excludePathPatterns("/mcp/v2/api-docs", "/mcp/swagger-resources", "/mcp/configuration/ui", "/mcp/configuration/security"); //不拦截swagger接口
    }
/*   @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }*/
}
