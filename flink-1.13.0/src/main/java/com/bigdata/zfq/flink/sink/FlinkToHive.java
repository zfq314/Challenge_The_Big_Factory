package com.bigdata.zfq.flink.sink;

import com.bigdata.zfq.flink.source.ClickSource;
import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;

/**
 * @ClassName FlinkToHive
 * @Description TODO
 * @Author ZFQ
 * @Date 2022/6/29 8:50
 * @Version 1.0
 **/
public class FlinkToHive {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);
        SingleOutputStreamOperator<Tuple2> map = executionEnvironment.addSource(new ClickSource()).map(new MapFunction<Event, Tuple2>() {
            @Override
            public Tuple2 map(Event value) throws Exception {
                return new Tuple2(value.user, 1L);
            }
        }).returns(Types.TUPLE(Types.STRING, Types.LONG));
        SingleOutputStreamOperator<Tuple2> streamOperator = map.keyBy(data -> data.f0)
                .sum(1);
        EnvironmentSettings settings = EnvironmentSettings.newInstance().inStreamingMode().build();
        StreamTableEnvironment tabEnv = StreamTableEnvironment.create(executionEnvironment, settings);
        HiveCatalog hiveCatalog = new HiveCatalog("myHive", "work_test", "D:\\idea-workspace\\Challenge_The_Big_Factory\\flink-1.13.0\\src\\main\\resources", "1.2.1");
        tabEnv.registerCatalog("myHive", hiveCatalog);
        tabEnv.useCatalog("myHive");
        tabEnv.getConfig().setSqlDialect(SqlDialect.HIVE);
        Table table = tabEnv.fromDataStream(streamOperator);
        tabEnv.createTemporaryView("demo", table);
        tabEnv.executeSql("select * from demo");

        executionEnvironment.execute();
    }
}
