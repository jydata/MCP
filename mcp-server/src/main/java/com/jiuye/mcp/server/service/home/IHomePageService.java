package com.jiuye.mcp.server.service.home;

import com.jiuye.mcp.server.model.home.HomeCommonEntity;
import com.jiuye.mcp.server.model.home.HomeFinenessStatisticEntity;
import com.jiuye.mcp.server.model.home.HomeSyncTimeEnity;
import com.jiuye.mcp.server.model.home.HomeTableCountEntity;
import com.jiuye.mcp.server.model.home.HomeSyncAgentErrorLogEntity;

import java.util.List;

/**
 * @author zp
 * @date 2018/12/11 0011
 */
public interface IHomePageService {

    /**
     * 查询各状态的job数量
     *
     * @return
     */
    List<HomeCommonEntity> queryJobs();

    /**
     * 查询源端、终端、路由数量
     *
     * @return
     */
    List<HomeCommonEntity> queryTechDatas();

    /**
     * 查询数据量对比
     *
     * @return
     */
    List<HomeFinenessStatisticEntity> queryTableCounts();

    /**
     * Agent Error Sql统计
     *
     * @return
     */
    List<HomeSyncAgentErrorLogEntity> queryAgentErrorSqlCounts();

    /**
     * 同步job数据折线图
     *
     * @return
     */
    List<HomeSyncTimeEnity> querySyncJobData();

    /**
     * 同步Agent数据折线图
     *
     * @return
     */
    List<HomeSyncTimeEnity> querySyncAgentData();


    /**
     * 两周内热度表集合
     *
     * @param fineness 时间粒度
     * @return
     */
    List<HomeTableCountEntity> queryHotTables(String fineness);
}
