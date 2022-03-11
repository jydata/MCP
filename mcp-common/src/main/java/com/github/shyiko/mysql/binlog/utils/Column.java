package com.github.shyiko.mysql.binlog.utils;

/**
 * Created by jepson ON 2018/9/12 5:20 PM.
 */
public class Column {


    //private static  StringBuffer sb = new StringBuffer();

    /**
     * add by dove on 2019/10/08
     *
     * @param r
     * @return
     */
    public static Object getValue(Object r) {
        StringBuffer sb = new StringBuffer();
        System.out.println("[Dove] create a sb :" + sb.toString());

        if (r == null) {
            return null;
        }
        switch (r.getClass().getSimpleName()) {
            case "String":
            case "byte[]":
                sb.append("" + r.toString().replaceAll(",|\'|\"|\t|\n|\r|\r\n|\\\\|:|\\{|}", " ") + "").append("");
                break;
            case "Integer":
            case "BigInteger":
            case "Long":
            case "Float":
            case "Double":
            case "BigDecimal":

            case "Boolean":
                return r;

            case "Date":
            case "Time":
            case "Timestamp":
                sb.append(r);
                break;
        }
        return sb.toString();
    }
    /**
     * 主要是为了write/delete语句， 字符串加 单引号，这样在发送给agent端，无需进行字段类型解析
     *
     * @param row
     * @return
     */
    public static String getColumnValue(Object[] row) {
        //sb.setLength(0);

        StringBuffer sb = new StringBuffer();

        sb.append("(");
        for (Object r : row) {
            if (r == null) {

                sb.append("null").append(",");

            } else {
                //System.out.println(r.getClass().getName());
                //https://link.jianshu.com/?t=https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
                switch (r.getClass().getSimpleName()) {
                    case "String":
                    case "byte[]":
                        sb.append("'" + r.toString().replaceAll(",|\'|\"|\t|\n|\r|\r\n|\\\\|:|\\{|}", " ") + "'").append(",");//去除逗号 单引号
                        break;

                    case "Integer":
                    case "BigInteger":
                    case "Long":
                    case "Float":
                    case "Double":
                    case "BigDecimal":

                    case "Boolean":
                        sb.append(r).append(",");
                        break;

                    case "Date":
                    case "Time":
                    case "Timestamp":
                        sb.append("'" + r + "'").append(",");
                        break;

                }
            }

        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");

        return sb.toString();

    }
}
