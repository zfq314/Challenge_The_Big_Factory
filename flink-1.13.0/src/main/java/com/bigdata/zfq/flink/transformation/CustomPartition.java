package com.bigdata.zfq.flink.transformation;

import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/17 下午 2:15
 * @version: 1.0
 */

public class CustomPartition {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.fromElements(1, 2, 3, 4, 5, 6, 7, 8).partitionCustom(new Partitioner<Integer>() {
                                                                     @Override
                                                                     public int partition(Integer key, int numPartitions) {
                                                                         return key % 2;
                                                                     }
                                                                 }, new KeySelector<Integer, Integer>() {
                                                                     @Override
                                                                     public Integer getKey(Integer value) throws Exception {
                                                                         return value;
                                                                     }
                                                                 }
        ).print("customerPartition").setParallelism(4);

        env.execute();
    }
}
