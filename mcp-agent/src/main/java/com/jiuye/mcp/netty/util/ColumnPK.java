package com.jiuye.mcp.netty.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jepson
 * @date 2018/9/27 2:17 PM
 */

@Component
public class ColumnPK {

    /**
     * map转成string  agent转成
     *
     * @param dbTableColPK
     * @return
     */
    public String mapConvertString(ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> dbTableColPK) {
        StringBuffer colPK = new StringBuffer();

        for (String dbTableName : dbTableColPK.keySet()) {
            colPK.append(dbTableName).append("@");
            for (Integer pos : dbTableColPK.get(dbTableName).keySet()) {
                colPK.append(pos).append("#");
            }
            colPK.append("%");
        }

        //去除最后一个拼接%
        if (colPK.length() > 0) {
            colPK.deleteCharAt(colPK.length() - 1);
        }
        return colPK.toString();
    }


}
