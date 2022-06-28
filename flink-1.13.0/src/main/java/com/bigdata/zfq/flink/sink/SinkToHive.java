package com.bigdata.zfq.flink.sink;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;

/**
 * @ClassName SinkToHive
 * @Description TODO
 * @Author ZFQ
 * @Date 2022/6/28 14:04
 * @Version 1.0
 **/
public class SinkToHive {
    public static void main(String[] args) {
        EnvironmentSettings sets  = EnvironmentSettings.newInstance().useBlinkPlanner().inBatchMode().build();
        TableEnvironment table = TableEnvironment.create(sets);
        HiveCatalog hive = new HiveCatalog("myhive", "work_test", "/hive/conf","1.2.1");
        table.registerCatalog("myhive",hive); //注册hiveCatalog
        table.getConfig().setSqlDialect(SqlDialect.HIVE);
        table.useCatalog("myhive");
        table.useDatabase("work_test");
        table.executeSql("show tables").print();
    }
}
