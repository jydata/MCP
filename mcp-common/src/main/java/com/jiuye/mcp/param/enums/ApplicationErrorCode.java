package com.jiuye.mcp.param.enums;

/**
 * 应用信息
 *
 * @author kevin
 * @date 2018-07-23
 */
public enum ApplicationErrorCode {

    INVALID_ARGUMENTS("INVALID_ARGUMENTS", "Invalid parameter！"),
    ILLEGAL_ARGUMENTS("ILLEGAL_ARGUMENTS", "Illegal parameter: "),
    MISS_PARAM("MISS_PARAM","Missing parameter: vo is empty！"),

    QUERY_ERROR("QUERY_ERROR","Query failed！"),
    UPDATE_ERROR("UPDATE_ERROR","Updated failed！"),
    DELETE_ERROR("DELETE_ERROR","Deleted failed！"),
    CREATE_ERROR("CREATE_ERROR","Save failed！"),
    RESET_ERROR("RESET_ERROR","Reset failed！"),
    LOGIN_ERROR("LOGIN_ERROR","Login failed！"),
    LOGOUT_ERROR("LOGOUT_ERROR","Logout failed！"),

    USER_INVALID_STATUS("USER_INVALID_STATUS", "User is disabled！"),
    MISS_USER_EMAIL("MISS_USER_EMAIL", "Email is required！"),
    MISS_USER_PHONE("MISS_USER_PHONE", "Phone is required！"),
    MISS_USER_STATUS("MISS_USER_STATUS", "Please select user status！"),
    MISS_USER_ROLE("MISS_USER_ROLE", "Please select a user role！"),
    MISS_USER_PWD("MISS_USER_PWD", "Password is required！"),
    PWD_SAME_ERROR("PWD_SAME_ERROR", "New password and old password cannot be the same！"),
    EMAIL_PHONE_EXIST("EMAIL_PHONE_EXIST", "Email or mobile phone number has been duplicated, please modify.！"),
    SAME_USER_ERROR("SAME_USER_ERROR", "The username already exists and cannot be added！"),
    TOKEN_AUTHENTICATION_ERROR("TOKEN_AUTHENTICATION_ERROR","Identity authentication failed, please log in again！"),
    USER_PWD_ERROR("USER_PWD_ERROR","The username or password is incorrect！"),

    MISS_RULE_SUFFIX("MISS_RULE_SUFFIX","Rule suffix is required！"),
    TALE_RULE_SUFFIX_NULL("TALE_RULE_SUFFIX_NULL","Database table names do not contain rule suffixes！"),
    TRANSFORM_ERROR("TRANSFORM_ERROR","The rule intercepts the length beyond the field！"),
    SAME_ADD_ERROR("SAME_ROUTE_ERROR", "The same record information already exists in the database and cannot be added！"),
    SAME_EDIT_ERROR("SAME_EDIT_ERROR", "The same record information already exists in the database and cannot be edited！"),
    SAME_DATA_ERROR("SAME_DATA_ERROR", "The data record of the same name already exists in the database and cannot be continued！"),
    SAME_ROUTE_STATUS_ERROR("SAME_ROUTE_STATUS_ERROR", "A routing record with the same name already exists in the database. This route cannot be enabled！"),
    NO_PRIMAY_ERROR("NO_PRIMAY_ERROR", "There is a table with no primary key in the table you are converting, and you cannot batch convert it！"),
    CREATE_SCHEMA_ERROR("CREATE_SCHEMA_ERROR","Create target schema table error！"),
    MISS_ROUTENAME("MISS_ROUTENAME", "Route name is empty！"),
    MISS_SCHEMANAME("MISS_SCHEMANAME", "The schema name is empty or contains invalid characters！"),
    MISS_TARGETNAME("MISS_TARGETNAME", "The target name is empty！"),
    MISS_USER_NAME("MISS_USER_NAME", "Username is required！"),
    TARGET_GENERATION_ERROR("UPDATE_ERROR","Table generation failed！"),

    CREATE_JOB_ERROR("CREATE_JOB_ERROR","Save job failed！"),
    UPDATE_JOB_ERROR("UPDATE_JOB_ERROR","Modify job failed！"),
    CREATE_GROUP_ERROR("CREATE_GROUP_ERROR","Save job group failed！"),
    QUERY_TARGET_SCHEMA_ERROR("QUERY_TARGET_SCHEMA_ERROR","Terget schema information is not queried！"),
    SYNC_TABLE_NULL("SYNC_TABLE_NULL","There is no synchronizable table！"),
    CREATE_SYNC_TABLE_ERROR("CREATE_SYNC_TABLE_ERROR","Save full sync table failed！"),
    UPDATE_SYNC_TABLE_ERROR("UPDATE_SYNC_TABLE_ERROR","Modify full sync table failed！"),
	NODATA_ERROR("NODATA_ERROR","No data in the MCP table corresponding to the route！"),
    JOB_NAME_EXIST_MSG("JOB_NAME_EXIST_MSG","Job name already exists！"),
    JOB_RUNNING("JOB_RUNNING","The current source library's DDL Job is running！"),
    JOB_INCREORFULL_RUNNING("JOB_INCREORFULL_RUNNING","The current source library's INCRE OR FULL Job is running！"),

	NOT_FOUND_APP("NOT_FOUND_APP","Application information does not exist！"),
	DROP_TABLE_ERROR("DROP_TABLE_ERROR","Drop table failed！");

	String code;
    String message;

    ApplicationErrorCode(String code, String message) {
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
