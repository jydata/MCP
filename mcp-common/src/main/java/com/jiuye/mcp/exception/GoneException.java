package com.jiuye.mcp.exception;

/**
 * 410 [GET]：用户请求的资源被永久删除，且不会再得到的。
 * 
 * @author ningyu
 * @date 2017年2月16日 下午3:10:14
 */
public class GoneException extends BizException {

    /**
     */
    private static final long serialVersionUID = -620319202937232187L;

    public GoneException(String code, String message) {
        super(code, message);
    }

}
