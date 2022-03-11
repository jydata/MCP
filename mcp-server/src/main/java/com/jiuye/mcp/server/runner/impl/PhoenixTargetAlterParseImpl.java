package com.jiuye.mcp.server.runner.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import com.alibaba.druid.util.JdbcConstants;
import com.jiuye.mcp.server.runner.ITargetAlterParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author jepson
 * @date 2018/9/14 1:20 PM
 *
 * modify by dove at 2019-10-10
 * 维护 ddl 的自动化解析
 */

@Component
public class PhoenixTargetAlterParseImpl implements ITargetAlterParse {

    private final static Logger logger = LoggerFactory.getLogger(PhoenixTargetAlterParseImpl.class);

    private String srcDbTableName;
    private String targetDbTableName;

    String columnTypeStr = "";

    /**
     * parser mysql alter sql to phoenix alter sql
     * <p>
     * https://dev.mysql.com/doc/refman/5.7/en/alter-table.html
     * https://issues.apache.org/jira/browse/PHOENIX-4815
     * support:
     * --添加多列
     * alter table test add (c1 char(1),c2 char(1));   --正确，add支持多列
     * alter table test add column (c1 char(1),c2 char(1));    --正确
     * alter table test add c1 char(1),add c2 char(1);     --正确
     * <p>
     * --删除多列
     * alter table test drop column c1,drop column c2;     --正确
     * alter table test drop c1,drop c2;   --错误
     * <p>
     * --修改多列
     * alter table test change c1 c3 char(1),change c2 c4 char(1);     --正确
     * alter table test change column c1 c3 char(1),change column c2 c4 char(1);       --正确
     * <p>
     * <p>
     * add --> add
     * modify --> modify     https://issues.apache.org/jira/browse/PHOENIX-4815  需要验证
     * drop --> drop
     * change --> drop+add
     *
     * @param sql
     * @return
     */
    @Override
    public String alterSQLParse(Map<String, String> ddlRulesMap, String sourceDatabase, String sql) {
        StringBuffer resultSql = new StringBuffer();

        try {
            StringBuffer alterSql = new StringBuffer();
            String dbType = JdbcConstants.MYSQL;

            // 清洗 定位到alter
            // 去除  /* ApplicationName=DBeaver 4.3.4 - Main */
            // sql = sql.replaceAll("/*.+?\\*/|#alter sql|\n", "").trim();
            sql = sql.replaceAll("`", "");
            sql = sql.substring(sql.toLowerCase().indexOf("alter"));

            String result = SQLUtils.format(sql, dbType);
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
            for (SQLStatement stmt : stmtList) {
                if (stmt instanceof SQLAlterTableStatement) {
                    //对应的源端db.table
                    if (null == ((SQLAlterTableStatement) stmt).getSchema()) {
                        srcDbTableName = sourceDatabase.toLowerCase() + "." + ((SQLAlterTableStatement) stmt).getTableName().toLowerCase();
                    } else {
                        srcDbTableName = ((SQLAlterTableStatement) stmt).getSchema().toLowerCase()
                                + "." + ((SQLAlterTableStatement) stmt).getTableName().toLowerCase();
                    }

                    //对应的终端db.table
                    targetDbTableName = ddlRulesMap.get(srcDbTableName);
                    if (targetDbTableName != null) {
                        List<SQLAlterTableItem> alterItem = ((SQLAlterTableStatement) stmt).getItems();
                        for (SQLAlterTableItem alter : alterItem) {
                            if (alter instanceof SQLAlterTableAddColumn) {
                                //add column
                                alterSql.setLength(0);
                                alterSql.append("ALTER TABLE ").append(targetDbTableName).append(" ADD ");
                                // add multi column
                                for (SQLColumnDefinition column : ((SQLAlterTableAddColumn) alter).getColumns()) {
                                    alterSql.append(columnType(column)).append(",");
                                }
                                alterSql.deleteCharAt(alterSql.length()-1);
                                alterSql.append(";");
                                resultSql.append(alterSql);
                            } else if (alter instanceof SQLAlterTableDropColumnItem) {
                                //drop column
                                alterSql.setLength(0);
                                alterSql.append("ALTER TABLE ").append(targetDbTableName).append(" DROP COLUMN ");
                                // drop multi column
                                for (SQLName column : ((SQLAlterTableDropColumnItem) alter).getColumns()) {
                                    alterSql.append(column.getSimpleName()).append(";");
                                }
                                resultSql.append(alterSql);
                            } else if (alter instanceof MySqlAlterTableModifyColumn) {
                                alterSql.setLength(0);
                                alterSql.append("ALTER TABLE ").append(targetDbTableName).append(" DROP COLUMN ");
                                alterSql.append(((MySqlAlterTableModifyColumn) alter).getNewColumnDefinition().getName()).append(";");

                                alterSql.append("ALTER TABLE ").append(targetDbTableName).append(" ADD ");
                                alterSql.append(columnType(((MySqlAlterTableModifyColumn) alter).getNewColumnDefinition())).append(";");

                                resultSql.append(alterSql);
                            } else if (alter instanceof MySqlAlterTableChangeColumn) {
                                //modify column
                               /* alterSql.setLength(0);
                                alterSql.append("ALTER TABLE ").append(targetDbTableName).append(" MODIFY ");
                                alterSql.append(columnType(((MySqlAlterTableModifyColumn) alter).getNewColumnDefinition()));
                                */

                                //phoenix not support modify and change grammar，so drop+add
                                alterSql.setLength(0);
                                alterSql.append("ALTER TABLE ").append(targetDbTableName).append(" DROP COLUMN ");
                                alterSql.append(((MySqlAlterTableChangeColumn) alter).getColumnName()).append(";");

                                alterSql.append("ALTER TABLE ").append(targetDbTableName).append(" ADD ");
                                alterSql.append(columnType(((MySqlAlterTableChangeColumn) alter).getNewColumnDefinition())).append(";");

                                resultSql.append(alterSql);
                            } else {
                                //other alter sql
                                logger.info("Not parser alter sql:\n" + sql + "\nAlter Type:" + alter.getClass().getSimpleName());
                            }
                        }
                    } else {
                        logger.warn("\nParser AlterSQL:\n{} \nThe {} table is not in the synchronous list,has been filtered out.", sql, srcDbTableName);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("\nParser AlterSQL:\n" + sql + "\nParser Error Message:\n" + ex.getMessage());
            resultSql.setLength(0);
        }

        logger.info("Parser AlterSQL: " + resultSql.toString().trim());

        return resultSql.toString().trim();
    }


    /**
     * column type
     *
     * @param column
     * @return
     */
    public String columnType(SQLColumnDefinition column) {
        switch (column.getDataType().getName().toLowerCase()) {
            case "tinytext":
            case "text":
            case "mediumtext":
            case "longtext":
            case "char":
            case "varchar":
                columnTypeStr = column.getName() + " varchar";
                break;
            case "bigint":
                columnTypeStr = column.getName() + " bigint";
                break;
            case "tinyint":
                columnTypeStr = column.getName() + " tinyint";
                break;
            case "smallint":
                columnTypeStr = column.getName() + " smallint";
            case "mediumint":
            case "int":
            case "integer":
                columnTypeStr = column.getName() + " integer";
                break;
            case "date":
            case "datetime":
            case "timestamp":
                columnTypeStr = column.getName() + " timestamp";
                break;
            case "float":
                columnTypeStr = column.getName() + " float";
                break;
            case "double":
                columnTypeStr = column.getName() + " double";
                break;
            case "decimal":
                columnTypeStr = column.getName() + " " + column.getDataType();
                if (column.getDefaultExpr() != null) {
                    columnTypeStr = columnTypeStr + " DEFAULT " + column.getDefaultExpr().toString().replaceAll("\'","");
                }
                break;
            case "tinyblob":
            case "blob":
            case "mediumblob":
            case "longblob":
            case "binary":
            case "varbinary":
                columnTypeStr = column.getName() + " varbinary";
                break;
            default:
                columnTypeStr = column.toString();
                break;
        }

        return columnTypeStr.replaceAll("`", "");
    }

    public String getSrcDbTableName() {
        return srcDbTableName;
    }

    public String getTargetDbTableName() {
        return targetDbTableName;
    }
}
