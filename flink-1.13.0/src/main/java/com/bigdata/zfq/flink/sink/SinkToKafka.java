package com.bigdata.zfq.flink.sink;

import com.bigdata.zfq.flink.source.ClickSource;
import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Properties;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/17 下午 4:17
 * @version: 1.0
 */

public class SinkToKafka {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        Properties properties = new Properties();
        //kafka地址输入
        properties.setProperty("bootstrap.servers", "hadoop31:9092");
        //properties.setProperty("group.id", "consumer-group");
        //properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // properties.setProperty("auto.offset.reset", "latest");

        DataStreamSource<String> click = env.readTextFile(ClassLoader.getSystemResource("log4j.properties").getPath());
        click.addSink(new FlinkKafkaProducer<String>("click", new SimpleStringSchema(), properties));
        DataStreamSource<Event> eventDataStreamSource = env.addSource(new ClickSource());
        //单输出流运算 SingleOutputStreamOperator
        SingleOutputStreamOperator<String> myData = eventDataStreamSource.map(data -> data.toString());
        myData.addSink(new FlinkKafkaProducer<String>("click", new SimpleStringSchema(), properties));

        env.execute();
    }
}
