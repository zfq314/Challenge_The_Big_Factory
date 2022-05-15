package com.bigdata.zfq.flink.source;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/15 下午 4:46
 * @version: 1.0
 */

public class SourceKafka {
    public static void main(String[] args) throws Exception {
        //Flink官方提供了连接工具flink-connector-kafka
        //读取链接其他程序的数据源，需要相应connector
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "hadoop31:9092");
        properties.setProperty("group.id", "consumer-group");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset", "latest");
        DataStreamSource<String> topic = env.addSource(new FlinkKafkaConsumer<String>("qrtz_scheduler_state", new SimpleStringSchema(), properties));
        topic.print("kafak-topic");
        env.execute();
    }
}
