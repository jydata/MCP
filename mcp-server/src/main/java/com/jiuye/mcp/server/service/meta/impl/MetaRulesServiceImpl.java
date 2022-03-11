package com.jiuye.mcp.server.service.meta.impl;

import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.param.enums.DeleteStatusEnum;
import com.jiuye.mcp.param.enums.MetaRuleTypeEnum;
import com.jiuye.mcp.server.dao.meta.MetaRulesMapper;
import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.meta.MetaRulesEntity;
import com.jiuye.mcp.server.service.meta.IMetaRulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-11-16
 */
@Service
public class MetaRulesServiceImpl implements IMetaRulesService {

    private static final Logger logger = LoggerFactory.getLogger(MetaRulesServiceImpl.class.getName());

    @Autowired
    private MetaRulesMapper metaRulesMapper;

    @Override
    public List<String> queryNameList() {
        return metaRulesMapper.queryNameList();
    }

    /**
     * 加载Rules列表
     */
    @Override
    public List<MetaRulesEntity> queryList() {
        return metaRulesMapper.queryList();
    }

    /**
     * 查询ule信息
     */
    @Override
    public MetaRulesEntity queryByName(String ruleName) {
        MetaRulesEntity param = new MetaRulesEntity();
        param.setRuleName(ruleName);
        param.setRuleStatus(DeleteStatusEnum.NO.getCode());
        return metaRulesMapper.query(param);
    }

    /**
     * 保存
     * @param entity
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ValidateResult save(MetaRulesEntity entity) {
        String ruleType = entity.getRuleType();
        // check rule type、name
        ValidateResult validateResult = checkRule(ruleType, entity.getRuleName());
        if (!validateResult.isFlag()){
            return validateResult;
        }

        // set comment
        entity.setComment(MetaRuleTypeEnum.maps.get(ruleType));
        // save rule
        int saveNum = metaRulesMapper.save(entity);
        if (saveNum != 1){
            validateResult = new ValidateResult(false, ApplicationErrorCode.CREATE_ERROR.getMessage());
        }

        return validateResult;
    }


    /**
     * 校验规则类型、名称
     *
     * @param ruleType
     * @param ruleName
     * @return
     */
    private ValidateResult checkRule(String ruleType, String ruleName){
        // check rule type enum
        if (!MetaRuleTypeEnum.maps.containsKey(ruleType)){
            return new ValidateResult(false, ApplicationErrorCode.ILLEGAL_ARGUMENTS.getMessage() + ruleType);
        }

        // type、name唯一性校验
        MetaRulesEntity paramEntity = new MetaRulesEntity();
        paramEntity.setRuleName(ruleName);
        paramEntity.setRuleStatus(DeleteStatusEnum.NO.getCode());
        MetaRulesEntity entity = metaRulesMapper.query(paramEntity);
        if (null != entity){
            return new ValidateResult(false, ApplicationErrorCode.SAME_ADD_ERROR.getMessage());
        }

        return new ValidateResult(true, null);
    }

    /**
     * 删除Rules信息
     */
    @Override
    public boolean delete(MetaRulesEntity entity){
        int delNum = metaRulesMapper.delete(entity);

        return delNum == 1 ? true : false;
    }

}
