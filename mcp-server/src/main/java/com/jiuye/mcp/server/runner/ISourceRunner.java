package com.jiuye.mcp.server.runner;


import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.jiuye.mcp.server.model.SourceRunnerParam;

/**
 * @author jepson
 * @date 2018/9/28 11:12 AM
 */
public interface ISourceRunner {

    BinaryLogClient fromSourceIncre(SourceRunnerParam param) throws Exception;

    void fromSourceFull(SourceRunnerParam param) throws Exception;

}
