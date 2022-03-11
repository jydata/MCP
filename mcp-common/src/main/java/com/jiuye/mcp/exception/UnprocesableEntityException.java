package com.jiuye.mcp.exception;

/**
 * 422 [POST/PUT/PATCH] 当创建一个对象时，发生一个验证错误。
 * 
 * @author ningyu
 * @date 2017年2月16日 下午3:18:19
 */
public class UnprocesableEntityException extends BizException {

    /**
     */
    private static final long serialVersionUID = -494459553306862812L;

    public UnprocesableEntityException(String code, String message) {
        super(code, message);
    }

}
