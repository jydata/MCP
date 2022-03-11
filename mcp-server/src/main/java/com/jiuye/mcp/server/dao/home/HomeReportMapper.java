package com.jiuye.mcp.server.dao.home;

import com.jiuye.mcp.server.model.home.HomeFinenessStatisticEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author dove
 * @date 2019-07-02
 */
@Component
public interface HomeReportMapper {

    List<HomeFinenessStatisticEntity> queryFinenessStatis();

}
