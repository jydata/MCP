package com.jiuye.mcp.exception;

/**
 * 403 [*]：表示得到授权（与401错误相对），但是访问是被禁止的。
 * 
 * @author ningyu
 * @date 2017年2月16日 下午3:02:11
 */
public class ForbiddenException extends BizException {

    /**
     */
    private static final long serialVersionUID = -6405385868371310478L;

    public ForbiddenException(String code, String message) {
        super(code, message);
    }
}
