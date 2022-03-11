package com.jiuye.mcp.exception;

/**
 * 401 [*]：表示没有权限（令牌、用户名、密码错误，或任何资源没有权限）
 * 
 * @author ningyu
 * @date 2017年2月16日 下午2:54:18
 */
public class UnauthorizedException extends BizException {

    /**
     */
    private static final long serialVersionUID = -6554672710870856572L;

    public UnauthorizedException(String code, String message) {
        super(code, message);
    }

}
