package com.jiuye.mcp.server.model.home;

import java.io.Serializable;

/**
 * 首页-通用实体
 * 1.Job执行状态占比
 * 3.Tech Metadata数据量
 *
 * @author zp
 * @date 2018/12/11 0011
 */
public class HomeCommonEntity implements Serializable {

    private static final long serialVersionUID = 532419961500615569L;

    /**
     * 状态名称
     * 1.Job
     * 1)init    : 初始
     * 2)wait    : 等待执行
     * 3)running : 运行中
     * 4)success : 成功
     * 5)fail    : 失败
     * <p>
     * 3.Tech Metadata
     * 1)源端链接个数
     * 2)终端链接个数
     * 3)路由数量(有效的)
     * 4)agent个数(截止时间点)
     */
    private String name;

    /**
     * 各种状态数据的条数
     */
    private Long value;

    public HomeCommonEntity() {
    }

    public HomeCommonEntity(String name, Long value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
