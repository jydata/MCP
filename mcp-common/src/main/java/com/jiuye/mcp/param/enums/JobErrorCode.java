package com.jiuye.mcp.param.enums;

/**
 * 作业常量信息
 *
 * @author kevin
 * @date 2018-07-23
 */
public enum JobErrorCode {

    JOB_NOT_EXISTS_ERROR("JOB_NOT_EXISTS_ERROR", "Job does not exist!"),
    JOB_ENABLE_DISABLE_ERROR("JOB_ENABLE_DISABLE_ERROR", "Job is disabled and operation is not allowed!"),
    JOB_UPDATE_ERROR("JOB_UPDATE_ERROR", "Update job error!"),
    JOB_CRON_ERROR("JOB_CRON_ERROR", "Job Cron format error!"),
    JOB_ADD_EXCEPTION("JOB_ADD_EXCEPTION", "Job scheduling error!"),
    JOB_TRIGGER_EXCEPTION("JOB_TRIGGER_EXCEPTION", "Job execution error!"),
    JOB_PAUSE_FAIL("JOB_PAUSE_FAIL", "Job pause failed!"),
    JOB_PAUSE_EXCEPTION("JOB_PAUSE_EXCEPTION", "Job pause error!"),
    JOB_RESUME_FAIL("JOB_RESUME_FAIL", "Job resume failed!"),
    JOB_RESUME_EXCEPTION("JOB_RESUME_EXCEPTION", "Job resume error!"),
//    JOB_QUERY_NAMES_EXCEPTION("JOB_QUERY_NAMES_EXCEPTION", "Query job name error!"),
    JOB_LOG_QUERY_EXCEPTION("JOB_LOG_QUERY_EXCEPTION", "Query log error!"),
    JOB_KILL_FAIL("JOB_KILL_FAIL", "Job close failed!"),
    JOB_KILL_EXCEPTION("LOG_KILL_EXCEPTION", "Job close error!"),
    LOG_NOT_EXEXISTS_ERROR("LOG_NOT_EXEXISTS_ERROR", "The job has no running instances!");

    String code;
    String message;

    JobErrorCode(String code, String message) {
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


