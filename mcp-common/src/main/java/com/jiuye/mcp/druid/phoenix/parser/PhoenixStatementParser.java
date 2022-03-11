package com.jiuye.mcp.druid.phoenix.parser;


import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.dialect.phoenix.parser.PhoenixExprParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

/**
 * Created by wenshao on 16/9/13. (From Alibaba)
 * modify by dove on 2020-03-13
 */
public class PhoenixStatementParser extends SQLStatementParser {
    public PhoenixStatementParser(String sql) {
        super(new PhoenixExprParser(sql));
    }

    public PhoenixStatementParser(Lexer lexer) {
        super(new PhoenixExprParser(lexer));
    }

    @Override
    protected void parseInsertColumns(SQLInsertInto insert) {
        if (lexer.token() == Token.RPAREN) {
            return;
        }

        for (; ; ) {
            SQLName expr = this.exprParser.name();
            expr.setParent(insert);
            insert.getColumns().add(expr);

            if (lexer.token() == Token.IDENTIFIER) {
                String text = lexer.stringVal();
                if (text.equalsIgnoreCase("TINYINT")
                        || text.equalsIgnoreCase("BIGINT")
                        || text.equalsIgnoreCase("INTEGER")
                        || text.equalsIgnoreCase("DOUBLE")
                        || text.equalsIgnoreCase("DATE")
                        || text.equalsIgnoreCase("VARCHAR")) {
                    expr.getAttributes().put("dataType", text);
                    lexer.nextToken();
                } else if (text.equalsIgnoreCase("CHAR")) {
                    String dataType = text;
                    lexer.nextToken();
                    accept(Token.LPAREN);
                    SQLExpr char_len = this.exprParser.primary();
                    accept(Token.RPAREN);
                    dataType += ("(" + char_len.toString() + ")");
                    expr.getAttributes().put("dataType", dataType);
                }
            }

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }
    }

    /**
     * create by dove
     *
     * @return
     */
    @Override
    public SQLCreateTableParser getSQLCreateTableParser() {
        return new PhoenixCreateTableParser(this.exprParser);
    }
}
