package com.bigdata.zfq.flinkcdc;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/13 下午 11:03
 * @version: 1.0
 */

public class KafkaUtil {
    public static FlinkKafkaProducer<String> getKafkaProducer(String topic){
        String brokerList = ConfigurationUtils.getProperties("brokerList");
        return new FlinkKafkaProducer<String>(brokerList, topic, new SimpleStringSchema());
    }
}
