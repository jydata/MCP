package com.jiuye.mcp.server.dao.user;

import com.jiuye.mcp.server.model.user.UserInfoEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * metadata mapper
 * @author kevin
 */
@Component
public interface UserInfoMapper {

    /**
     * 根据登录用户权限加载对应的用户信息
     *
     * @param entity
     * @return
     */
    List<UserInfoEntity> queryManagerList(UserInfoEntity entity);

    /**
     * 根据条件查询对应的用户信息
     *
     * @param entity
     * @return
     */
    List<UserInfoEntity> queryList(UserInfoEntity entity);

    /**
     * 获取登录用户信息
     *
     * @param entity
     * @return
     */
    UserInfoEntity query(UserInfoEntity entity);


    /**
     * 　新增保存
     *
     * @param entity
     * @return
     */
    int save(UserInfoEntity entity);

    /**
     * 编辑
     *
     * @param entity
     * @return
     */
    int update(UserInfoEntity entity);

    /**
     * 重置密码
     *
     * @param entity
     * @return
     */
    int resetPassword(UserInfoEntity entity);

    /**
     * 修改信息校对Email和Phone的唯一性
     *
     * @param entity
     * @return
     */
    int checkEAP(UserInfoEntity entity);

    /**
     * 新增用户校对Email和Phone的唯一性
     *
     * @param entity
     * @return
     */
    int checkAddUser(UserInfoEntity entity);

    /**
     * 修改密码
     *
     * @param entity
     * @return
     */
    int updatePassword(UserInfoEntity entity);
}
