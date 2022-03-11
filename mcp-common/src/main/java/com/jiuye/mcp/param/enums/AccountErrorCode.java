package com.jiuye.mcp.param.enums;

/**
 * 账号信息
 *
 * @author kevin
 * @date 2018-07-23
 */
public enum AccountErrorCode {

    PERMISSION_DENIED("PERMISSION_DENIED","Permission denied！"),
    USERNAME_EXISTS("USERNAME_EXISTS","User name already exists！"),
    ACCOUNT_NOT_FIND("ACCOUNT_NOT_FIND","User name does not exist！"),
    ACCOUNT_PWD_ERROR("ACCOUNT_PWD_ERROR","User name password error！"),
    LOGIN_ERROR("LOGIN_ERROR","Login failed！"),
    LOGOUT_ERROR("LOGOUT_ERROR","Logout failed！"),
    MOBILE_EXISTS("MOBILE_EXISTS","Mobile number already exists！"),
    EMAIL_EXISTS("EMAIL_EXISTS","Email already exists！");
    
    String code;
    String message;

    AccountErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

}


