package com.jiuye.mcp.utils;

/**
 * @author yuhaiqing
 * @date 2018/8/17
 */
public class JsonUtil {

    public static Object parserStringValue(Object value) {
        if (value != null) {
            String str = String.valueOf(value);
            if (str.startsWith("\"") && str.endsWith("\"")) {
                return "'".concat(str.replaceAll("\"","")).concat("'");
            }
        }

        return value;
    }
}
