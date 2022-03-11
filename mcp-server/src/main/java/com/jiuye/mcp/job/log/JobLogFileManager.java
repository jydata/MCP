package com.jiuye.mcp.job.log;

import com.jiuye.mcp.server.model.job.JobLogInfoEntity;
import com.jiuye.mcp.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;

/**
 * job log file manager
 *
 * @author kevin
 * @date 2018-10-23
 */
public class JobLogFileManager {

    private static final Logger logger = LoggerFactory.getLogger(JobLogFileManager.class.getName());

    public static final InheritableThreadLocal<String> jobFileHolder = new InheritableThreadLocal<>();

    private static String basePath = "log/job";

    /**
     * init job log filepath
     *
     * @param filePath
     */
    public static void initPath(String filePath) {
        if (StringUtils.isNotBlank(filePath)) {
            basePath = filePath;
        }

        File basePathDir = new File(basePath);
        if (!basePathDir.exists()) {
            basePathDir.mkdirs();
        }

        basePath = basePathDir.getPath();
    }

    public static String getPath() {
        return basePath;
    }

    /**
     * generate job log fileName
     *
     * @param date
     * @param logId
     * @return
     */
    public static String generateFileName(Date date, long logId) {
        String dateStr = DateUtil.getFormatDate(date, DateUtil.DF_YYYY_MM_DD);
        File filePath = new File(getPath(), dateStr);
        if (!filePath.exists()) {
            filePath.mkdir();
        }

        StringBuffer fileName = new StringBuffer(filePath.getPath())
                .append(File.separator)
                .append(String.valueOf(logId))
                .append(".log");

        return fileName.toString();
    }

    /**
     * 写日志文件
     *
     * @param fileName
     * @param log
     */
    public static void writeLog(String fileName, String log) {
        if (StringUtils.isBlank(fileName)) {
            return;
        }

        // 创建日志文件
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("write log create file is error", e);
                return;
            }
        }

        // 作业日志为null
        if (null == log) {
            log = "";
        }
        log += "\r\n";

        // 将作业日志写到文件
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file, true);
            os.write(log.getBytes("utf-8"));
            os.flush();
        } catch (Exception e) {
            logger.error("wirte job log file is error", e);
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.error("close log file stream is error", e);
                }
            }
        }
    }

    public static JobLogInfoEntity readLog(String fileName, int fromLineNum) {
        if (StringUtils.isBlank(fileName)) {
            return new JobLogInfoEntity(fromLineNum, 0, "read log fail, log file not found", true);
        }

        File logFile = new File(fileName);
        if (!logFile.exists()) {
            return new JobLogInfoEntity(fromLineNum, 0, "read log fail, log file not exists", true);
        }

        StringBuffer logBuffer = new StringBuffer();
        int toLineNum = 0;
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new InputStreamReader(new FileInputStream(logFile), "utf-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                toLineNum = reader.getLineNumber();
                if (toLineNum >= fromLineNum) {
                    logBuffer.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            logger.error("read job log file is error", e);
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("close log reader is error", e);
                }
            }
        }

        return new JobLogInfoEntity(fromLineNum, toLineNum, logBuffer.toString(), false);
    }
}
