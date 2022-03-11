package com.jiuye.mcp.param.enums;

/**
 * 元数据信息
 *
 * @author kevin
 * @date 2018-07-23
 */
public enum MetadataErrorCode {

    PORT_IS_NULL("PORT_IS_NULL","Port field is requried！"),
    IP_IS_NULL("IP_IS_NULL","IP field is requried！"),
    LINK_NAME_IS_NULL("LINK_NAME_IS_NULL","Username field is requried！"),
    DECRYPT_ERROR("DECRYPT_ERROR","Password field is requried！"),
    DB_INVALID_ARGUMENTS("ACCOUNT_NOT_FIND","Invalid database connection！"),
    ACCESS_ERROR("ACCESS_ERROR","Database access failed！"),
    LOAD_DRIVER_ERROR("LOAD_DRIVER_ERROR","Loading database driver failed！"),
    CONNECTION_NULL_ERROR("CONNECTION_NULL_ERROR","Source connection info does not exist!"),
    CONNECTION_TIMEOUT_ERROR("CONNECTION_TIMEOUT_ERROR","Database connection timeout !"),
    CONNECTION_CLOSE_ERROR("CONNECTION_CLOSE_ERROR","Close database connection failed！"),
    TEST_CONNECTION_ERROR("TEST_CONNECTION_ERROR","Test database connection failed！"),
    LOAD_METADATA_ERROR("LOAD_METADATA_ERROR","Extract metadata failed！"),
    ADD_DUPLICATE_ERROR("ADD_DUPLICATE_ERROR","Data duplication！"),
    GET_CONNECTION_ERROR("GET_CONNECTION_ERROR", "Get database connection failed!"),
    EXECUTEQUERY_SQLEXCEPTION("EXECUTEQUERY_SQLEXCEPTION", "Execute Query SQLException!"),
    CHECK_ENV_META_SUCCESS("CHECK_ENV_META_SUCCESS","Metadata synchronization environment check successfully！"),
    CHECK_ENV_META_ERROR("CHECK_ENV_META_ERROR","Metadata synchronization environment check failed！"),
    CHECK_ENV_DDL_SUCCESS("CHECK_ENV_DDL_SUCCESS", "DDL batch conversion environment check successfully！"),
    CHECK_ENV_DDL_ERROR("CHECK_ENV_DDL_ERROR", "DDL batch conversion environment check failed！");

    String code;
    String message;

    MetadataErrorCode(String code, String message) {
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


