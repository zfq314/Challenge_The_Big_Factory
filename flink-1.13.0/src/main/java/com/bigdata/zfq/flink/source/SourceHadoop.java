package com.bigdata.zfq.flink.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/15 下午 5:22
 * @version: 1.0
 */

public class SourceHadoop {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //读取hadoop的数据源
        DataStreamSource<String> stringDataStreamSource = env.readTextFile("hdfs://mycluster/data/dolphinscheduler/root/resources/call_process.sh");
        stringDataStreamSource.print();

        env.execute();
    }
}
