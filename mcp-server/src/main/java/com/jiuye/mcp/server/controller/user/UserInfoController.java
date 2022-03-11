package com.jiuye.mcp.server.controller.user;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.param.constants.SystemConstant;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.response.Response;
import com.jiuye.mcp.server.controller.BaseController;
import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.user.UserInfoEntity;
import com.jiuye.mcp.server.service.user.IUserInfoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 元数据信息
 *
 * @author zg
 * @date 2018-10-19
 */
@RestController
@RequestMapping(value = "/user", produces = {"application/json;charset=UTF-8"})
public class UserInfoController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class.getName());

    @Autowired
    private IUserInfoService userInfoService;

    /**
     * 用户登录
     *
     * @param request
     * @param userInfo
     * @return
     */
    @ApiOperation(value = "用户登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Response<String> login(HttpServletRequest request, @ApiParam(value = "用户登录信息", required = true) @RequestBody() UserInfoEntity userInfo) {
        if (null == userInfo) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<String> response = new Response<>();
        String sessionId = request.getSession().getId();
        try {
            ValidateResult validateResult = userInfoService.login(userInfo, sessionId);
            if (validateResult.isFlag()) {
                // response token
                response.setItems(validateResult.getMessage());
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            } else {
                response.setMessage(validateResult.getMessage());
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            }
        } catch (Exception e) {
            logger.error("Login is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.LOGIN_ERROR.getCode(), ApplicationErrorCode.LOGIN_ERROR.getMessage());
        }
        return response;
    }

    /**
     * 用户登出
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Response<String> logout(@RequestParam(value = "token") String token) {
        if (null == token) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<String> response = new Response<>();
        try {
            boolean flag = userInfoService.logout(token);
            if (flag) {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage(ApplicationErrorCode.LOGOUT_ERROR.getMessage());
            }
        } catch (Exception e) {
            logger.error("Logout is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.LOGOUT_ERROR.getCode(), ApplicationErrorCode.LOGOUT_ERROR.getMessage());
        }
        return response;
    }

    /**
     * 获取登录用户信息
     */
    @ApiOperation(value = "获取登录用户信息")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public Response<UserInfoEntity> query(@ApiParam(value = "登录用户信息", required = false) @RequestBody() UserInfoEntity searchEntity) {

        UserInfoEntity entity = userInfoService.query(searchEntity);

        Response<UserInfoEntity> response = Response.createResponse(entity);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        return response;
    }

    /**
     * 根据条件查询对应的用户信息
     *
     * @return
     */
    @ApiOperation(value = "通过条件查询用户信息")
    @RequestMapping(value = "/query_list", method = RequestMethod.POST)
    public Response<List<UserInfoEntity>> queryList(@ApiParam(value = "user查询条件实体") @RequestBody() UserInfoEntity searchEntity) {

        List<UserInfoEntity> list = userInfoService.queryList(searchEntity);

        Response<List<UserInfoEntity>> response = Response.createResponse(list);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        return response;
    }

    /**
     * 根据登录用户权限加载对应的用户信息
     *
     * @return
     */
    @ApiOperation(value = "根据登录用户权限加载对应的用户信息")
    @RequestMapping(value = "/query_manager_list", method = RequestMethod.POST)
    public Response<List<UserInfoEntity>> queryManagerPage(@ApiParam(value = "user实体类", required = true) @RequestBody() UserInfoEntity searchEntity) {

        List<UserInfoEntity> list = userInfoService.queryManagerList(searchEntity);

        Response<List<UserInfoEntity>> response = Response.createResponse(list);
        response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
        return response;
    }

    /**
     * User新增保存
     *
     * @param entity
     * @return
     */
    @ApiOperation(value = "新增用户")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Response<UserInfoEntity> save(HttpServletRequest request, @ApiParam(value = "user实体类", required = true) @RequestBody(required = true) UserInfoEntity entity) {
        if (null == entity) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<UserInfoEntity> response = new Response<>();
        try {
            String userName = getUser(request);
            entity.setCreateUser(userName);
            entity.setUpdateUser(userName);
            ValidateResult validateResult = userInfoService.save(entity);
            if (!validateResult.isFlag()) {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
                response.setMessage("Email or mobile phone number has been duplicated, please modify.");
            } else {
                response.setItems(entity);
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }
        } catch (Exception e) {
            response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            logger.error("Add user is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
        }
        return response;
    }

    /**
     * User编辑
     *
     * @param entity
     * @return
     */
    @ApiOperation(value = "编辑用户")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response<UserInfoEntity> update(HttpServletRequest request, @ApiParam(value = "user实体类", required = true) @RequestBody(required = true) UserInfoEntity entity) {
        if (null == entity) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<UserInfoEntity> response = new Response<>();
        try {
            entity.setUpdateUser(getUser(request));
            ValidateResult validateResult = userInfoService.update(entity);
            if (!validateResult.isFlag()) {
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            } else {
                response.setItems(entity);
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }
        } catch (Exception e) {
            logger.error("update user is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.UPDATE_ERROR.getCode(), ApplicationErrorCode.UPDATE_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 更新用户状态
     *
     * @param entity
     * @return
     */
    @ApiOperation(value = "更新用户状态")
    @RequestMapping(value = "/update_status", method = RequestMethod.POST)
    public Response<UserInfoEntity> updateStatus(HttpServletRequest request, @ApiParam(value = "user实体类", required = true) @RequestBody(required = true) UserInfoEntity entity) {
        if (null == entity || 0L == entity.getUserId()) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<UserInfoEntity> response = new Response<>();
        try {
            entity.setUpdateUser(getUser(request));
            ValidateResult validateResult = userInfoService.updateStatus(entity);
            if (!validateResult.isFlag()) {
                response.setMessage(validateResult.getMessage());
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            } else {
                response.setItems(entity);
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }
        } catch (Exception e) {
            logger.error("Update user status is error!", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.UPDATE_ERROR.getCode(), ApplicationErrorCode.UPDATE_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 重置密码
     *
     * @param entity
     * @return
     */
    @ApiOperation(value = "重置密码")
    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    public Response<UserInfoEntity> resetPassword(HttpServletRequest request, @ApiParam(value = "user实体类", required = true) @RequestBody(required = true) UserInfoEntity entity) {
        if (null == entity || 0L == entity.getUserId()) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<UserInfoEntity> response = new Response<>();
        try {
            entity.setUpdateUser(getUser(request));
            ValidateResult validateResult = userInfoService.resetPassword(entity);
            if (!validateResult.isFlag()){
                response.setMessage(validateResult.getMessage());
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            }else {
                response.setItems(entity);
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
            }
        } catch (Exception e) {
            logger.error("Reset password is error！", e);
            if (e instanceof BizException) {
                throw new InvalidRequestException(((BizException) e).getCode(), e.getMessage());
            }
            throw new InvalidRequestException(ApplicationErrorCode.RESET_ERROR.getCode(), ApplicationErrorCode.RESET_ERROR.getMessage());
        }

        return response;
    }

    /**
     * 检查密码并修改密码
     *
     * @param entity
     * @return
     */
    @ApiOperation(value = "修改密码")
    @RequestMapping(value = "update_password", method = RequestMethod.POST)
    public Response<UserInfoEntity> updatePassword(HttpServletRequest request, @ApiParam(value = "user实体类", required = true) @RequestBody(required = true) UserInfoEntity entity) {
        if (null == entity) {
            throw new InvalidRequestException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        Response<UserInfoEntity> response = new Response<>();
        try {
            // 验证输入的用户名和密码校对正确，调用updatePassword方法修改密码
            ValidateResult validateResult = userInfoService.updatePassword(entity, getUser(request));
            if (!validateResult.isFlag()) {
                response.setMessage(validateResult.getMessage());
                response.setCode(SystemConstant.RESPONSE_CODE_ERROR);
            } else {
                response.setCode(SystemConstant.RESPONSE_CODE_SUCCESS);
                response.setItems(entity);
            }
        } catch (Exception e) {
            logger.error("Password change failed！", e);
            response.setCode((SystemConstant.RESPONSE_CODE_ERROR));
        }
        return response;
    }
}