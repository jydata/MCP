package com.jiuye.mcp.server.dao.home;

import com.jiuye.mcp.server.model.home.HomeCommonEntity;
import com.jiuye.mcp.server.model.home.HomeTableCountEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 首页面板
 *
 * @author zp
 * @date 2018/12/11 0011
 */
@Component
public interface HomePageMapper {

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
     * 热度表(Top 10)
     *
     * @param fineness 时间粒度
     * @return
     */
    List<HomeTableCountEntity> queryHotTables(String fineness);
}
