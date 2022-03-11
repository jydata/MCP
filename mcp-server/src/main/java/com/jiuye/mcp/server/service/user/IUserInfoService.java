package com.jiuye.mcp.server.service.user;

import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.user.UserInfoEntity;

import java.util.List;

/**
 * @author zhaopeng
 */
public interface IUserInfoService {

    /**
     * 获取用户信息
     *
     * @param entity
     * @return
     */
    UserInfoEntity query(UserInfoEntity entity);

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
     * 保存
     *
     * @param entity
     * @return
     */
    ValidateResult save(UserInfoEntity entity);

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    ValidateResult update(UserInfoEntity entity);


    /**
     * 修改状态
     *
     * @param entity
     * @return
     */
    ValidateResult updateStatus(UserInfoEntity entity);

    /**
     * 重置密码
     *
     * @param entity
     * @return
     * @throws Exception
     */
    ValidateResult resetPassword(UserInfoEntity entity) throws  Exception;

    /**
     * 修改密码
     *
     * @param entity
     * @param operator
     * @return
     * @throws Exception
     */
    ValidateResult updatePassword(UserInfoEntity entity, String operator) throws  Exception;

    /**
     * 登录
     *
     * @param searchEntity
     * @param sessionId
     * @return
     */
    ValidateResult login(UserInfoEntity searchEntity, String sessionId);

    /**
     * 根据token从redis删除用户信息
     *
     * @param token
     * @return
     */
    boolean logout(String token);
}
