package com.jiuye.mcp.exception;

/**
 * 400 [POST/PUT/PATCH]：用户发出的请求有错误（常用在请求必要的参数错误上），服务器没有进行新建或修改数据的操作，该操作是幂等的。
 * 
 * @author ningyu
 * @date 2017年2月16日 下午3:14:48
 */
public class InvalidRequestException extends BizException {

    /**
     */
    private static final long serialVersionUID = 7736869550788854505L;

    public InvalidRequestException(String code, String message) {
        super(code, message);
    }

}
