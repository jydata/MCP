package com.jiuye.mcp.server.model;

import java.io.Serializable;

/**
 * 校验结果实体类
 *
 * @author kevin
 * @date 2018-10-29
 */
public class ValidateResult implements Serializable{

    private static final long serialVersionUID = 4347696930744343322L;

    private boolean flag;
    private String message;

    public ValidateResult() {
    }

    public ValidateResult(boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
