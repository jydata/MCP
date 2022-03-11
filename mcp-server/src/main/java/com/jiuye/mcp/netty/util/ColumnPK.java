package com.jiuye.mcp.netty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jepson
 */
@Component
public class ColumnPK {
    private static final Logger log = LoggerFactory.getLogger(ColumnPK.class);

    private ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> dbTableColPK = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>>();

    public ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> getDbTableColPK() {
        return dbTableColPK;
    }

    public void setDbTableColPK(ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> dbTableColPK) {
        this.dbTableColPK = dbTableColPK;
    }


    /**
     * 接收最新的string 转成 map server接收，然后转成
     *
     * @param columnPKStr
     * @return
     */
    public ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> stringConvertMap(String columnPKStr) {

        //  columnPKStr = "1.omsprd.ali_address@0#%1.omsprd.bdf2_blob_store@0#3#%1.omsprd.bdf2_clob_store@0#";
        ConcurrentHashMap<Integer, String> colPK;

        String tableName;
        String[] tablePK;
        try{
            for (String row : columnPKStr.split("%")) {
                tablePK = row.split("@");
                if(tablePK.length == 2){
                    tableName = tablePK[0];
                    colPK = new ConcurrentHashMap<Integer, String>();

                    for (String pk : tablePK[1].split("#")) {
                        colPK.put(Integer.valueOf(pk), "PRI");

                    }
                    dbTableColPK.put(tableName, colPK); //相同key 存放会被覆盖，这样就是最新的

                }
              }

        }catch (Exception e){
            log.error(e.toString());
        }

        return dbTableColPK;

    }


}
