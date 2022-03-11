package com.jiuye.mcp.response;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 接口返回的统一对象
 * 
 * @param <T>
 *            需要返回的业务对象
 * 
 * @author ningyu
 * @date 2016年12月27日 下午12:00:53
 */
public class Response<T> implements Serializable {

    /**
	 */
    private static final long serialVersionUID = -2010077110826408295L;

    /**
     * 错误编码 </br>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Response() {
        this.code = "";
        this.message = "";
        this.items = (T) new HashMap();
    }

    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> Response<T> createResponse(T t) {
        Response<T> response = new Response<T>();
        response.setItems(t);
        return response;
    }

    /**
     * 异常编码
     */
    private String code = "";

    /**
     * 异常信息
     */
    private String message = "";

    /**
     * 具体的数据对象
     */
    private T items = null;

    /**
     * @return the 异常编码
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     *            the 异常编码 to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the 异常信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the 异常信息 to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the 具体的数据对象
     */
    public T getItems() {
        return items;
    }

    /**
     * @param items
     *            the 具体的数据对象 to set
     */
    public void setItems(T items) {
        this.items = items;
    }
}
