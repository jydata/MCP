package com.jiuye.mcp.exception;

/**
 * 406 [GET]：用户请求的格式不可得（比如用户请求JSON格式，但是只有XML格式）或（请求参数需要数字，用户传入字符串）
 * 
 * @author ningyu
 * @date 2017年2月16日 下午3:07:18
 */
public class NotAcceptableException extends BizException {

    /**
     */
    private static final long serialVersionUID = -1539180649319488205L;

    public NotAcceptableException(String code, String message) {
        super(code, message);
    }

}
