package com.bigdata.zfq.flink.sink;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;

/**
 * @ClassName FlinkReadHive
 * @Description TODO
 * @Author ZFQ
 * @Date 2022/6/28 14:04
 * @Version 1.0
 **/
public class FlinkReadHive {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings sets  = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment table = StreamTableEnvironment.create(executionEnvironment, sets);
        HiveCatalog hive = new HiveCatalog("myhive", "work_test", "/hive/conf/","1.2.1");
        table.registerCatalog("myhive",hive); //注册hiveCatalog
        table.getConfig().setSqlDialect(SqlDialect.HIVE);
        table.useCatalog("myhive");
        table.useDatabase("work_test");
        table.executeSql("show tables").print();
        executionEnvironment.execute();
    }
}
