package com.jiuye.mcp.server.service.user.impl;

import com.alibaba.fastjson.JSON;
import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.param.enums.DeleteStatusEnum;
import com.jiuye.mcp.server.dao.user.UserInfoMapper;
import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.user.UserInfoEntity;
import com.jiuye.mcp.server.service.user.IUserInfoService;
import com.jiuye.mcp.utils.DateUtil;
import com.jiuye.mcp.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 元数据信息service实现类
 *
 * @author zhaopeng
 */
@Service
public class UserInfoServiceImpl implements IUserInfoService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public ValidateResult login(UserInfoEntity entity, String sessionId) {
        if (StringUtils.isBlank(entity.getUserName())) {
            throw new BizException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        String key = generateUserKey(entity.getUserName(), entity.getPassword());
        String userInfo = (String) redissonClient.getBucket(key).get();
        // 用户名失效或未登录
        if (userInfo == null) {
            UserInfoEntity user = userInfoMapper.query(entity);
            if (user == null) {
                return new ValidateResult(false, ApplicationErrorCode.USER_PWD_ERROR.getMessage());
            }

            // 表示用户被禁用
            if (user.getUserStatus().equals(DeleteStatusEnum.NO.getCode())) {
                return new ValidateResult(false, ApplicationErrorCode.USER_INVALID_STATUS.getMessage());
            }

            // 查到并且用户名和密码的大小写一致则生成token，保存到前端
            user.setSessionID(sessionId);
            redissonClient.getBucket(key).set(JSON.toJSONString(user));
            // 设置key的过期时间,一般半个小时---expire 设置生存时间（单位/秒）,pexpire 设置生存时间(单位/毫秒)
            redissonClient.getSet(key).expire(SystemConstant.MAX_REDIS_VALIDTIME, TimeUnit.MILLISECONDS);
        } else {
            // 用户名存在于redis
            UserInfoEntity user = JSON.parseObject(userInfo, UserInfoEntity.class);
            // 比較sessionId
            String oldSessionId = user.getSessionID();
            if (!sessionId.equals(oldSessionId)) {
                redissonClient.getBucket(key).set(null);
                redissonClient.getBucket(key).expireAt(0);
                user.setSessionID(sessionId);
                redissonClient.getBucket(key).set(JSON.toJSONString(user));
            }
            redissonClient.getSet(key).expire(SystemConstant.MAX_REDIS_VALIDTIME, TimeUnit.MILLISECONDS);
        }

        return new ValidateResult(true, key);
    }

    /**
     * 根据token从redis删除用户信息
     *
     * @param token
     * @return
     */
    @Override
    public boolean logout(String token) {
        String redisValue = (String) redissonClient.getBucket(token).get();
        if (redisValue != null) {
            redissonClient.getBucket(token).delete();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据登录用户权限加载对应的用户信息
     *
     * @param entity
     * @return
     */
    @Override
    public List<UserInfoEntity> queryManagerList(UserInfoEntity entity) {
        return userInfoMapper.queryManagerList(entity);
    }

    /**
     * 获取登录用户信息
     *
     * @param entity
     * @return
     */
    @Override
    public UserInfoEntity query(UserInfoEntity entity) {
        return userInfoMapper.query(entity);
    }

    /**
     * 根据条件查询对应的用户信息
     *
     * @return
     */
    @Override
    public List<UserInfoEntity> queryList(UserInfoEntity entity) {
        return userInfoMapper.queryList(entity);
    }

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    @Override
    public ValidateResult save(UserInfoEntity entity) {
        // 非空
        ValidateResult validateResult = checkSaveInfo(entity);
        if (!validateResult.isFlag()) {
            return validateResult;
        }

        // 用户名是否存在
        validateResult = checkUserNameExist(entity.getUserName());
        if (!validateResult.isFlag()) {
            return validateResult;
        }

        // 校对Email和Phone的唯一性
        int exist = userInfoMapper.checkAddUser(entity);
        if (exist > 0) {
            return new ValidateResult(false, ApplicationErrorCode.EMAIL_PHONE_EXIST.getMessage());
        }

        String curTime = DateUtil.getFormatDate(DateUtil.DF_YYYYMMDD_HHMMSS);
        entity.setCreateTime(curTime);
        entity.setUpdateTime(curTime);
        entity.setUserStatus("0");
        int saveNum = userInfoMapper.save(entity);
        if (saveNum != 1) {
            return new ValidateResult(false, ApplicationErrorCode.CREATE_ERROR.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 修改用户信息
     *
     * @param entity
     * @return
     */
    @Override
    public ValidateResult update(UserInfoEntity entity) {
        // 非空
        ValidateResult validateResult = checkUpdateInfo(entity);
        if (!validateResult.isFlag()) {
            return validateResult;
        }

        int flag = userInfoMapper.checkEAP(entity);
        if (flag > 0) {
            return new ValidateResult(false, ApplicationErrorCode.EMAIL_PHONE_EXIST.getMessage());
        }

        // update
        entity.setUpdateTime(DateUtil.getFormatDate(DateUtil.DF_YYYYMMDD_HHMMSS));
        int updateNum = userInfoMapper.update(entity);
        if (updateNum != 1) {
            return new ValidateResult(false, ApplicationErrorCode.UPDATE_ERROR.getMessage());
        }

        return new ValidateResult(true, null);
    }

    @Override
    public ValidateResult updateStatus(UserInfoEntity entity) {
        entity.setUpdateTime(DateUtil.getFormatDate(DateUtil.DF_YYYYMMDD_HHMMSS));
        int updateNum = userInfoMapper.update(entity);
        if (updateNum != 1) {
            return new ValidateResult(false, ApplicationErrorCode.UPDATE_ERROR.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 重置密码
     *
     * @param entity
     * @throws Exception
     */
    @Override
    public ValidateResult resetPassword(UserInfoEntity entity) throws Exception {
        entity.setUpdateTime(DateUtil.getFormatDate(DateUtil.DF_YYYYMMDD_HHMMSS));
        entity.setDefaultPassword("123456");
        int updateNum = userInfoMapper.resetPassword(entity);
        if (updateNum != 1) {
            return new ValidateResult(false, ApplicationErrorCode.UPDATE_ERROR.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 修改密码
     *
     * @param entity
     * @throws Exception
     */
    @Override
    public ValidateResult updatePassword(UserInfoEntity entity, String operator) throws Exception {
        // 非空
        ValidateResult validateResult = checkNullUserName(entity.getUserName());
        if (!validateResult.isFlag()) {
            return validateResult;
        }

        entity.setPassword(entity.getOldPassword());
        UserInfoEntity userInfo = userInfoMapper.query(entity);
        if (null == userInfo) {
            return new ValidateResult(false, ApplicationErrorCode.USER_PWD_ERROR.getMessage());
        }

        //修改数据库密码
        entity.setUpdateUser(operator);
        int updateNum = userInfoMapper.updatePassword(entity);
        if (updateNum != 1) {
            return new ValidateResult(false, ApplicationErrorCode.UPDATE_ERROR.getMessage());
        }

        //删除Redis信息
        String key = generateUserKey(entity.getUserName(), entity.getOldPassword());
        redissonClient.getBucket(key).delete();

        return new ValidateResult(true, null);
    }

    /**
     * 生成redis user key
     *
     * @param userName
     * @param password
     * @return
     */
    private String generateUserKey(String userName, String password) {
        String md5name = MD5Util.toMD5(userName);
        return "mcp_user_" + md5name + "_" + password;
    }

    /**
     * 校验用户名是否存在
     *
     * @param userName
     * @return
     */
    private ValidateResult checkUserNameExist(String userName) {
        UserInfoEntity param = new UserInfoEntity();
        param.setUserName(userName);
        UserInfoEntity userEntity = userInfoMapper.query(param);
        if (null != userEntity) {
            return new ValidateResult(false, ApplicationErrorCode.SAME_USER_ERROR.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 校验保存用户信息
     *
     * @param entity
     * @return
     */
    private ValidateResult checkSaveInfo(UserInfoEntity entity) {
        // 用户名、email、phoned
        ValidateResult validateResult = checkUpdateInfo(entity);
        if (!validateResult.isFlag()) {
            return validateResult;
        }

        // role
        validateResult = checkNullRole(entity.getUserRole());
        if (!validateResult.isFlag()) {
            return validateResult;
        }

        // pwd
        validateResult = checkNullPwd(entity.getPassword());
        if (!validateResult.isFlag()) {
            return validateResult;
        }

        return validateResult;
    }

    /**
     * 校验用户名、email、phone非空
     *
     * @param entity
     * @return
     */
    private ValidateResult checkUpdateInfo(UserInfoEntity entity) {
        // 用户名
        ValidateResult validateResult = checkNullUserName(entity.getUserName());
        if (!validateResult.isFlag()) {
            return validateResult;
        }

        // email
        validateResult = checkNullEmail(entity.getEmail());
        if (!validateResult.isFlag()) {
            return validateResult;
        }

        // phone
        validateResult = checkNullPhone(entity.getPhone());
        if (!validateResult.isFlag()) {
            return validateResult;
        }

        return validateResult;
    }

    /**
     * 校验用户名非空
     *
     * @param userName
     * @return
     */
    private ValidateResult checkNullUserName(String userName) {
        if (StringUtils.isBlank(userName)) {
            return new ValidateResult(false, ApplicationErrorCode.MISS_USER_NAME.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 校验邮箱非空
     *
     * @param email
     * @return
     */
    private ValidateResult checkNullEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return new ValidateResult(false, ApplicationErrorCode.MISS_USER_EMAIL.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 校验手机号非空
     *
     * @param phone
     * @return
     */
    private ValidateResult checkNullPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return new ValidateResult(false, ApplicationErrorCode.MISS_USER_PHONE.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 校验角色非空
     *
     * @param role
     * @return
     */
    private ValidateResult checkNullRole(String role) {
        if (StringUtils.isBlank(role)) {
            return new ValidateResult(false, ApplicationErrorCode.MISS_USER_ROLE.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 校验密码非空
     *
     * @param password
     * @return
     */
    private ValidateResult checkNullPwd(String password) {
        if (StringUtils.isBlank(password)) {
            return new ValidateResult(false, ApplicationErrorCode.MISS_USER_PWD.getMessage());
        }

        return new ValidateResult(true, null);
    }

}