package com.jiuye.mcp.exception;

/**
 * 404 [*]：用户发出的请求针对的是不存在的记录，服务器没有进行操作，该操作是幂等的。
 * 
 * @author ningyu
 * @date 2017年2月16日 下午3:03:15
 */
public class NotFoundException extends BizException {

    /**
     */
    private static final long serialVersionUID = -4868822122652150392L;

    public NotFoundException(String code, String message) {
        super(code, message);
    }

}
