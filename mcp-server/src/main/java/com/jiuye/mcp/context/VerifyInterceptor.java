package com.jiuye.mcp.context;

import com.alibaba.fastjson.JSON;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.server.model.user.UserInfoEntity;
import com.sun.istack.internal.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 请求拦截器
 *
 * @author zhonggang
 */
public class VerifyInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(VerifyInterceptor.class);

    /**
     * 无效用户信息状态码
     */
    private static int INVALID_CODE = 666;

    /**
     * 账号多次登录状态码
     */
    private static int MULTIPLE_CODE = 999;

    private static List<String> IGNORE_URI = new ArrayList<>();
    static {
        IGNORE_URI.add("/mcp/user/login");
        IGNORE_URI.add("/mcp/error");
        IGNORE_URI.add("/mcp/swagger-resources");
        IGNORE_URI.add("/mcp/v2/api-docs");
        IGNORE_URI.add("/mcp/configuration/security");
        IGNORE_URI.add("/mcp/configuration/ui");
        IGNORE_URI.add("/mcp/images/favicon-16x16.png");
        IGNORE_URI.add("/mcp/images/favicon-32x32.png");
    }

    @Autowired
    RedissonClient redissonClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // 非拦截路由检查
        if (IGNORE_URI.contains(uri)) { return true;  }

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        // 获取当前sessionId
        String sessionId = request.getSession().getId();
        if (StringUtils.isEmpty(sessionId)){
            response.setStatus(INVALID_CODE);
            return false;
        }

        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            response.setStatus(INVALID_CODE);
            return false;
        }

        // 用户信息失效返回到登录页面
        String value = (String)redissonClient.getBucket(token).get();
        if(StringUtils.isEmpty(value)){
            response.setStatus(INVALID_CODE);
            return false;
        }

        UserInfoEntity userInfo = JSON.parseObject(value, UserInfoEntity.class);
        if (null == userInfo){
            response.setStatus(INVALID_CODE);
            return false;
        }

        String userSessionId = userInfo.getSessionID();
        if(StringUtils.isNotEmpty(userSessionId) && sessionId.equals(userSessionId)){
            redissonClient.getBucket(token).expire(SystemConstant.MAX_REDIS_VALIDTIME, TimeUnit.MILLISECONDS);
            return true;
        }else {
            response.setStatus(MULTIPLE_CODE);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }

}
