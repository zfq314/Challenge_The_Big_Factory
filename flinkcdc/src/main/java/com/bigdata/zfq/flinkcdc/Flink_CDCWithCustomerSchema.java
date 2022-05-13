package com.bigdata.zfq.flinkcdc;


import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/13 下午 9:32
 * @version: 1.0
 */

public class Flink_CDCWithCustomerSchema {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        DebeziumSourceFunction<String> mysqlSource = MySqlSource.<String>builder()
                .hostname("hadoop31")
                .port(3306)
                .username("root")
                .password("hadoopdb-hadooponeoneone@dc.com.")
                .databaseList("dolphinscheduler")
                .tableList("dolphinscheduler.qrtz_scheduler_state")
                .startupOptions(StartupOptions.initial())
                //自定义序列化器
                .deserializer(new DebeziumDeserializationSchema<String>() {
                    @Override
                    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {
                        //mysql_binlog_source.dolphinscheduler.qrtz_scheduler_state
                        String topic = sourceRecord.topic();
                        // 正则 \\.
                        String[] split = topic.split("\\.");
                        String databases = split[1];
                        String tableName = split[2];
                        //获取操作类型
                        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
                        //获取值信息并转换为Struct类型
                        Struct value = (Struct) sourceRecord.value();
                        //获取变化后的数据
                        Struct after = value.getStruct("after");
                        //创建json对象,存放数据
                        JSONObject data = new JSONObject();
                        for (Field field : after.schema().fields()) {
                            Object o = after.get(field);
                            data.put(field.name(), o);
                        }
                        //创建json对象封装最终的返回的对象
                        JSONObject result = new JSONObject();
                        result.put("operation", operation.toString().toLowerCase());
                        result.put("data", data);
                        result.put("database", databases);
                        result.put("table", tableName);
                        //发送数据到下游
                        collector.collect(result.toJSONString());

                    }

                    @Override
                    public TypeInformation<String> getProducedType() {
                        return TypeInformation.of(String.class);
                    }
                }).build();
        DataStreamSource<String> streamSource = env.addSource(mysqlSource);
        streamSource.print();
        //执行任务
        env.execute();
    }
}
