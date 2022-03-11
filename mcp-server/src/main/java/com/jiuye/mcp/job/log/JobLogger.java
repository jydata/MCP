package com.jiuye.mcp.job.log;

import com.jiuye.mcp.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * job logger
 *
 *
 * @author kevin
 * @date 2018-10-23
 */
public class JobLogger {

    private static final Logger logger = LoggerFactory.getLogger(JobLogger.class.getName());

    public static void log(String logPattern, Object ... logArguments){
        FormattingTuple mft = MessageFormatter.arrayFormat(logPattern, logArguments);
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String log = mft.getMessage();
        logInfo(ste, log);
    }

    public static void log(Throwable e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String logMsg = sw.toString();

        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        logInfo(callInfo, logMsg);
    }

    private static void logInfo(StackTraceElement ste, String log){
        StringBuffer strBuffer = new StringBuffer()
                .append(DateUtil.getFormatDate(DateUtil.DF_YYYYMMDD_HHMMSS))
                .append(" ")
                .append("["+ ste.getClassName() + "$" + ste.getMethodName() +"]").append("-")
                .append("["+ ste.getLineNumber() +"]").append("-")
                .append("["+ Thread.currentThread().getName() +"]").append(" ")
                .append(log != null ? log : "");
        String logInfo = strBuffer.toString();

        String fileName = JobLogFileManager.jobFileHolder.get();
        if (StringUtils.isNotBlank(fileName)){
            JobLogFileManager.writeLog(fileName, logInfo);
        }else {
            logger.info(">>>>>>job log fileName is null, log: {}", logInfo);
        }
    }
}
