package com.bigdata.zfq.flinkcdc;


import com.ververica.cdc.connectors.sqlserver.SqlServerSource;
import com.ververica.cdc.connectors.sqlserver.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * @ClassName SqlServerSourceExample
 * @Description TODO
 * @Author ZFQ
 * @Date 2022/6/23 10:13
 * @Version 1.0
 **/
//-- 查看数据库是否启用cdc
//        SELECT name,is_cdc_enabled FROM sys.databases WHERE is_cdc_enabled = 1;
//
//        -- 查看当前数据库表是否启用cdc
//        SELECT name,is_tracked_by_cdc FROM sys.tables WHERE is_tracked_by_cdc = 1;



//    -- 对当前数据库启用 CDC
//            USE MyDB
//            GO
//            EXECUTE sys.sp_cdc_enable_db;
//            GO
//            -- 对当前数据库禁用 CDC
//            USE MyDB
//            GO
//            EXEC sys.sp_cdc_disable_db
//            GO
public class SqlServerSourceExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //开启checkpoint或者savepoint
        env.enableCheckpointing(5000L);
        //指定CK的一致性语义
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //设置任务关闭的时候保留最后一次CK数据
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //指定从CK自动重启策略
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 2000L));
        //设置状态后端
        //客户端访问集群端口 9000 3.x 8020
        // 本地 checkpoint uri file:///D:/idea-workspace/Challenge_The_Big_Factory/checkpoint
        env.setStateBackend(new RocksDBStateBackend("hdfs://mycluster/flinkCDC",true));
        //设置访问HDFS的用户名
        System.setProperty("HADOOP_USER_NAME", "root");
        SourceFunction<String> sourceFunction = SqlServerSource.<String>builder()
                .hostname("192.168.27.88")
                .port(1433)
                .database("decent") // monitor sqlserver database
                .tableList("dbo.stu") // monitor products table
                .username("sa")
                .password("2015@dc.com")
                //startupOptions(StartupOptions.latest())
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();


        env.addSource(sourceFunction)
                .print().setParallelism(1); // use parallelism 1 for sink to keep message ordering

        env.execute();
    }

}
