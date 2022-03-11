package com.jiuye.mcp.server.util;

import com.jiuye.mcp.server.util.impl.MysqlJDBCConnectionImpl;
import com.jiuye.mcp.server.util.impl.PhoenixJDBCConnectionImpl;

/**
 * @author zhaopeng
 * @date 2018-12-17
 */
public class JDBCManager {

    public static final String MYSQL = "MySQL";
    public static final String PHOENIX = "Phoenix";

    public static final String INFORMATION_SCHEMA = "information_schema";

    public static IJDBCConnection factory(String type) {
        switch (type) {
            case MYSQL:
                return new MysqlJDBCConnectionImpl();
            case PHOENIX:
                return new PhoenixJDBCConnectionImpl();
        }
        return null;
    }

}
