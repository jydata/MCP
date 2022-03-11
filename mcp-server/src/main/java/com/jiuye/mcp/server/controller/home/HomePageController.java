package com.jiuye.mcp.server.controller.home;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.response.Response;
import com.jiuye.mcp.server.controller.BaseController;
import com.jiuye.mcp.server.dao.meta.MetaDatarouteMapper;
import com.jiuye.mcp.server.model.home.*;
import com.jiuye.mcp.server.model.meta.MetaDatarouteEntity;
import com.jiuye.mcp.server.service.home.IHomePageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.time.FastDateFormat;
import org.redisson.api.RList;
import org.redisson.api.RListMultimap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 首页面板
 * @author zp
 * @date 2018/12/11
 */
@RestController
@RequestMapping(value = "/home", produces = {"application/json;charset=UTF-8"})
public class HomePageController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(HomePageController.class.getName());

    @Autowired
    private IHomePageService homePageService;
    @Autowired
    private MetaDatarouteMapper metaDatarouteMapper;
    @Autowired
    RedissonClient redissonClient;

    /**
     * 1.查询作业状态图
     * 1)init    : 初始
     * 2)wait    : 等待执行
     * 3)running : 运行中
     * 4)success : 成功
     * 5)fail    : 失败
     *
     * @return
     */
    @ApiOperation(value = "查询作业状态图", notes = "查询作业状态图", httpMethod = "GET")
    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    public Response<List<HomeCommonEntity>> queryHomeJobsPage() {
        List<HomeCommonEntity> homeCommonEntities = homePageService.queryJobs();

        Response<List<HomeCommonEntity>> response = Response.createResponse(homeCommonEntities);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 2.同步数据折线图
     *
     * @return
     */
    @ApiOperation(value = "同步数据折线图", notes = "同步数据折线图", httpMethod = "GET")
    @RequestMapping(value = "/sync_data", method = RequestMethod.GET)
    public Response<List<HomeSyncTimeEnity>> queryHomeSyncDataPage() {

        RListMultimap<String, String> map = redissonClient.getListMultimap("monitorRecord");

        int mapSize = 500;

        long currentTime = System.currentTimeMillis();
        //截取时间格式当前时间的秒数
        int val = Long.valueOf(FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(currentTime).substring(17, 19)).intValue();
        long endTime = currentTime;
        //用于计算当前之前最近的取值时间
        int flag = (val + 3) % 3;
        switch (flag) {
            case 0:
                endTime = currentTime;
                break;
            case 1:
                endTime = currentTime - 1000;
                break;
            case 2:
                endTime = currentTime - 2000;
                break;
        }

        //构建一个包含取值时间的List，用于后续map的取值
        List<String> valTimeList = new ArrayList<>();
        FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        String timeFormat;
        for (int i = 0; i < mapSize; i++) {
            timeFormat = format.format(endTime);
            valTimeList.add(timeFormat);
            endTime -= 3000;
        }

        List<MetaDatarouteEntity> routeIdList = metaDatarouteMapper.queryRouteId();
        Map<Long, String> routeNameMap = new HashMap<>();
        if (routeIdList != null && routeIdList.size() > 0) {
            for (MetaDatarouteEntity valRoute : routeIdList) {
                routeNameMap.put(valRoute.getRouteId(), valRoute.getRouteName());
            }
        }

        List<HomeSyncTimeEnity> timeList = new ArrayList<>();
        HomeSyncRecordEntity entity;
        HomeSyncRouteEntity routeEntityInfo;
        HomeSyncTimeEnity timeEnityInfo;

        for (String time : valTimeList) {
            Map<Long, Long> routeMap = new Hashtable<>();
            RList<String> valList = map.get(time);

            String subTime = time.substring(11, 19);
            //计算map中同一个key下，route对应的Record sum值
            for (String homeSyncRecord : valList) {
                entity = JSON.parseObject(homeSyncRecord, HomeSyncRecordEntity.class);
                long routeId = entity.getRouteid();
                if (routeNameMap.containsKey(routeId)) {
                    if (routeMap.containsKey(routeId)) {
                        Long routeSum = routeMap.get(routeId);
                        long record = entity.getRecord();

                        routeMap.put(routeId, (routeSum + record));
                    } else {
                        routeMap.put(routeId, entity.getRecord());
                    }
                }
            }

            List<HomeSyncRouteEntity> routeList = new ArrayList<>();
            timeEnityInfo = new HomeSyncTimeEnity();
            timeEnityInfo.setTime(subTime);
            for (Map.Entry<Long, Long> mapEntry : routeMap.entrySet()) {
                routeEntityInfo = new HomeSyncRouteEntity();
                Long routeId = mapEntry.getKey();
                routeEntityInfo.setRouteId(routeId);
                routeEntityInfo.setRouteName(routeNameMap.get(routeId));
                routeEntityInfo.setRecordSum(mapEntry.getValue());
                routeList.add(routeEntityInfo);
            }
            timeEnityInfo.setRouteList(routeList);
            timeList.add(timeEnityInfo);
        }

        List<HomeSyncTimeEnity> list = Lists.reverse(timeList);

        Response<List<HomeSyncTimeEnity>> response = Response.createResponse(list);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 3.技术元数据柱状图
     * 1)源端链接个数
     * 2)终端链接个数
     * 3)路由数量(有效的)
     * 4)agent个数(截止时间点)
     *
     * @return
     */
    @ApiOperation(value = "技术元数据柱状图", notes = "技术元数据柱状图", httpMethod = "GET")
    @RequestMapping(value = "/tech_metadata", method = RequestMethod.GET)
    public Response<List<HomeCommonEntity>> queryHomeTechMetadataPage() {
        List<HomeCommonEntity> homeCommonEntities = homePageService.queryTechDatas();

        Response<List<HomeCommonEntity>> response = Response.createResponse(homeCommonEntities);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    @ApiOperation(value = "数据量统计对比", notes = "数据量统计对比", httpMethod = "GET")
    @RequestMapping(value = "/table_counts", method = RequestMethod.GET)
    public Response<List<HomeFinenessStatisticEntity>> queryHomeTableCountPage() {

        List<HomeFinenessStatisticEntity> homeTableCountEntities = homePageService.queryTableCounts();

        Response<List<HomeFinenessStatisticEntity>> response = Response.createResponse(homeTableCountEntities);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    @ApiOperation(value = "Agent Error Sql统计", notes = "Agent Error Sql统计", httpMethod = "GET")
    @RequestMapping(value = "/error_sql_counts", method = RequestMethod.GET)
    public Response<List<HomeSyncAgentErrorLogEntity>> queryHomeAgentErrorSqlCountsPage() {
        List<HomeSyncAgentErrorLogEntity> errorCounts = homePageService.queryAgentErrorSqlCounts();

        Response<List<HomeSyncAgentErrorLogEntity>> response = Response.createResponse(errorCounts);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 同步job数据折线图
     *
     * @return
     */
    @ApiOperation(value = "同步Job数据折线图", notes = "同步Job数据折线图", httpMethod = "GET")
    @RequestMapping(value = "/sync_job_data", method = RequestMethod.GET)
    public Response<List<HomeSyncTimeEnity>> queryHomeSyncJobDataPage() {
        List<HomeSyncTimeEnity> homeSyncTimeEnities = homePageService.querySyncJobData();

        Response<List<HomeSyncTimeEnity>> response = Response.createResponse(homeSyncTimeEnities);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }

    /**
     * 同步agent数据折线图
     * @return
     */
    @ApiOperation(value = "同步Agent数据折线图", notes = "同步Agent数据折线图", httpMethod = "GET")
    @RequestMapping(value = "/sync_agent_data", method = RequestMethod.GET)
    public Response<List<HomeSyncTimeEnity>> queryHomeSyncAgentDataPage(){
        List<HomeSyncTimeEnity> homeSyncTimeEnities = homePageService.querySyncAgentData();

        Response<List<HomeSyncTimeEnity>> response = Response.createResponse(homeSyncTimeEnities);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }


    @ApiOperation(value = "热度表", notes = "热度表", httpMethod = "GET")
    @RequestMapping(value = "/hot_tables", method = RequestMethod.GET)
    public Response<List<HomeTableCountEntity>> queryHomeHotTablesWeekTwoPage(@ApiParam(value = "粒度", required = true, defaultValue = "day") @RequestParam(value = "fineness") String fineness) {
        List<HomeTableCountEntity> homeHotTablesCountEnities = homePageService.queryHotTables(fineness);

        Response<List<HomeTableCountEntity>> response = Response.createResponse(homeHotTablesCountEnities);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);

        return response;
    }
}
