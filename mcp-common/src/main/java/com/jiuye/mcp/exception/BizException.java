package com.jiuye.mcp.exception;
/**
 * @author kevin
 * @date 2018-08-30
 */
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 6603630153337594538L;
    private String code = "";

    public BizException() {
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BizException(Throwable throwable) {
        super(throwable);
    }

    public String getCode() {
        return this.code;
    }
}
