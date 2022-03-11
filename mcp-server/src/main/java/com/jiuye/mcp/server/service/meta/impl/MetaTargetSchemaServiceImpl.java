/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuye.mcp.server.service.meta.impl;

import com.jiuye.mcp.datasource.PhoenixDataSourceV2;
import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.param.enums.DeleteStatusEnum;
import com.jiuye.mcp.server.dao.meta.MetaTargetSchemaMapper;
import com.jiuye.mcp.server.model.meta.MetaTargetSchemaEntity;
import com.jiuye.mcp.server.model.meta.TargetSchemaInfoEntity;
import com.jiuye.mcp.server.service.meta.IMetaTargetSchemaService;
import com.jiuye.mcp.server.util.JDBCManager;
import com.jiuye.mcp.utils.SpringBeanFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author kevin
 * 
 */
@Service
public class MetaTargetSchemaServiceImpl implements IMetaTargetSchemaService {

	private static final Logger logger = Logger.getLogger(MetaTargetSchemaServiceImpl.class.getName());

	@Autowired
	private MetaTargetSchemaMapper metaTargetSchemaMapper;

	/**
	 * 加载Target Schema信息
	 *
	 * @param entity
	 * @return
	 */
	@Override
	public List<MetaTargetSchemaEntity> queryList(MetaTargetSchemaEntity entity) {
		return metaTargetSchemaMapper.queryList(entity);
	}

	/**
	 * 查询终端信息
	 *
	 * @return
	 */
	@Override
	public List<TargetSchemaInfoEntity> queryList() {
		MetaTargetSchemaEntity param = new MetaTargetSchemaEntity();
		param.setDeleteFlag(DeleteStatusEnum.NO.getCode());
		param.setExecuteFlag(1);
		List<MetaTargetSchemaEntity> targetSchemaEntities = metaTargetSchemaMapper.queryList(param);

		List<TargetSchemaInfoEntity> list = new ArrayList<>();

		List<String> targetList = new ArrayList();
		for (MetaTargetSchemaEntity entity : targetSchemaEntities) {
			String targetName = entity.getTargetName();
			if (!targetList.contains(targetName)) {
				targetList.add(targetName);
			}
		}

		for (String targetName : targetList) {
			TargetSchemaInfoEntity schemaInfoEntity = new TargetSchemaInfoEntity();
			List<MetaTargetSchemaEntity> schemaList = new ArrayList<>();
			for (MetaTargetSchemaEntity metaTargetSchemaEntity : targetSchemaEntities) {
				if (targetName.equals(metaTargetSchemaEntity.getTargetName())) {
					schemaList.add(metaTargetSchemaEntity);
				}
			}
			schemaInfoEntity.setTargetName(targetName);
			schemaInfoEntity.setEntities(schemaList);
			list.add(schemaInfoEntity);
		}

		return list;
	}

	/**
	 * 新增Schema信息  1
	 * 修改Schema信息  2
	 * 修改Schema状态  3
	 */
	@Override
	public void saveSchemaInfo(List<MetaTargetSchemaEntity> schemaList, String operator) throws Exception {
		for (MetaTargetSchemaEntity entity : schemaList) {
			if (!entity.getSchemaName().contains("-") && !entity.getSchemaName().contains(" ")) {
				// 1-add || 2-update_execte || 3-update_delete
				if (entity.getFlag().equals("1")) {
					int cnt = metaTargetSchemaMapper.queryCount(entity);
					if (cnt == 0) {
						entity.setCreateUser(operator);
						metaTargetSchemaMapper.save(entity);
					} else {
						throw new BizException(ApplicationErrorCode.SAME_DATA_ERROR.getCode(), ApplicationErrorCode.SAME_DATA_ERROR.getMessage());
					}
				} else if (entity.getFlag().equals("2")) {
					// 判断是否唯一
					int cnt = metaTargetSchemaMapper.queryCount(entity);
					if (cnt == 0) {
						// update
						entity.setUpdateUser(operator);
						metaTargetSchemaMapper.update(entity);
					} else {
						throw new BizException(ApplicationErrorCode.SAME_DATA_ERROR.getCode(), ApplicationErrorCode.SAME_DATA_ERROR.getMessage());
					}
				} else if (entity.getFlag().equals("3")) {
					entity.setUpdateUser(operator);
					metaTargetSchemaMapper.update(entity);
				} else {
					logger.error("schema name is Error");
					throw new BizException(ApplicationErrorCode.MISS_SCHEMANAME.getCode(), ApplicationErrorCode.MISS_SCHEMANAME.getMessage());
				}
			}else {
				throw new InvalidRequestException("Error", "Schema name naming error, schema name cannot contain '-' and null characters");
			}
		}
	}


	/**
	 * 在终端生成Schema信息并修改ExecuteFlag
	 *
	 * @param list
	 * @param operator
	 * @throws Exception
	 */
	@Override
	public void createSchema(List<MetaTargetSchemaEntity> list, String operator) throws Exception {
		for (MetaTargetSchemaEntity entity : list) {
			if(entity.getDeleteFlag().equals("0")){
				throw  new InvalidRequestException("Error", "The schema is disabled and cannot be synchronized");
			}

			String schemaSQL = "CREATE SCHEMA IF NOT EXISTS " + entity.getSchemaName().toString();

			Connection conn = SpringBeanFactory.getBean(PhoenixDataSourceV2.class).phoenixJdbcConn();
			conn.createStatement().execute(schemaSQL);
			conn.commit();

			// 拼接连接map的key
			entity.setUpdateUser(operator);
			entity.setExecuteFlag(1);

			metaTargetSchemaMapper.update(entity);

			// 关闭数据库连接
			JDBCManager.factory("Phoenix").close(conn);
		}
	}

}
