package com.bigdata.zfq.flinkcdc;

import com.ververica.cdc.connectors.mysql.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/13 下午 11:05
 * @version: 1.0
 */

public class Flink2kafka {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        //flinkcdc构建SourceFunction
        DebeziumSourceFunction mysqlSourceFunction = MySqlSource.<String>builder()
                .hostname("10.10.80.31")
                .port(3306)
                .password("hadoopdb-hadooponeoneone@dc.com.")
                .username("root")
                .databaseList("dolphinscheduler")
                .tableList("dolphinscheduler.qrtz_scheduler_state,dolphinscheduler.t_ds_alert")// 具体的表
                .deserializer(new My_JsonDebeziumDeserializationSchema())//序列化方式,也可以子定义序列化方式
                .startupOptions(StartupOptions.latest())
                .build();
        DataStreamSource dataStreamSource = executionEnvironment.addSource(mysqlSourceFunction);

        //sink
        String topic = "qrtz_scheduler_state";
        dataStreamSource.addSink(KafkaUtil.getKafkaProducer(topic));

        dataStreamSource.print();

        executionEnvironment.execute("Flink2kafka");
        //flinkcdc数据同kafka
        //查看所有的topic
        //kafak-topic.sh --zookeeper hadoop31:2181 --list
        //kafka-console-consumer.sh --bootstrap-server hadoop31:9092 --topic qrtz_scheduler_state --from-beginning
        //公司提交代码
    }

}
