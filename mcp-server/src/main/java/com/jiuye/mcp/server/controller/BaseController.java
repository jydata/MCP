package com.jiuye.mcp.server.controller;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhaopeng
 * @date 2018-11-02
 */
public class BaseController {

    public String getUser(HttpServletRequest request){
        String userName = request.getHeader("mcp_user");
        if (StringUtils.isNotBlank(userName)){
            return userName;
        }
        return null;
    }
}
