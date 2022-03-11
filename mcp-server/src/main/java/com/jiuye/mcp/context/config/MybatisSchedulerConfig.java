package com.jiuye.mcp.context.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.jiuye.mcp.job.executor.QuartzManager;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL连接配置信息
 * @author Kevin
 * @date 2019-05-28 15:35
 */
@Configuration
@MapperScan(basePackages = "com.jiuye.mcp.server.dao", sqlSessionFactoryRef = "sqlSessionFactory")
public class MybatisSchedulerConfig {

    static final String CONFIG_LOCATION = "classpath:mybatis-config.xml";
    static final String MAPPER_LOCATION = "classpath:com/jiuye/mcp/**/dao/**/*Mapper.xml";
    static final String QUARTZ_LOCATION = "classpath:quartz.properties";

    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.driver-class-name}")
    private String jdbcDriverClassName;
    @Value("${spring.datasource.username}")
    private String jdbcUsername;
    @Value("${spring.datasource.password}")
    private String jdbcPassword;
    @Value("${spring.datasource.initialSize}")
    private int initialSize;
    @Value("${spring.datasource.minIdle}")
    private int minIdle;
    @Value("${spring.datasource.maxActive}")
    private int maxActive;
    @Value("${spring.datasource.maxWait}")
    private int maxWait;
    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;
    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;
    @Value("${spring.datasource.testWhileIdle}")
    private boolean testWhileIdle;
    @Value("${spring.datasource.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${spring.datasource.testOnReturn}")
    private boolean testOnReturn;
    @Value("${spring.datasource.filters}")
    private String filters;

    @Bean(name = "dataSource", destroyMethod = "close")
    public DataSource dataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(jdbcDriverClassName);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(jdbcUsername);
        dataSource.setPassword(jdbcPassword);
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(1800);
        dataSource.setLogAbandoned(true);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setFilters(filters);

        // 配置监控统计拦截的filters
        List<Filter> filterList = new ArrayList<>();

        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);

        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig);
        filterList.add(wallFilter);

        dataSource.setProxyFilters(filterList);

        return dataSource;
    }


    @Bean(name = "sqlSessionFactory")
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 加载mybatis的全局配置文件 
        sqlSessionFactoryBean.setConfigLocation(resolver.getResource(MybatisSchedulerConfig.CONFIG_LOCATION));
        // 设置mybatis的主配置文件
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(MybatisSchedulerConfig.MAPPER_LOCATION));

        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager() throws SQLException{
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean(name = "mcpScheduler")
    public SchedulerFactoryBean scheduler() throws Exception {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource());
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setStartupDelay(5);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContextKey");
        schedulerFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource(MybatisSchedulerConfig.QUARTZ_LOCATION));
        return schedulerFactoryBean;
    }

    @Bean(name = "quartzManager", initMethod = "init", destroyMethod = "destroy")
    public QuartzManager quartzManager() throws Exception {
        QuartzManager quartzManager = new QuartzManager();
        quartzManager.setScheduler(scheduler().getObject());
        return quartzManager;
    }
}
