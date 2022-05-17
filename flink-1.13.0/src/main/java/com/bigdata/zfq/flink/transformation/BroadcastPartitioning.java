package com.bigdata.zfq.flink.transformation;

import com.bigdata.zfq.flink.source.ClickSource;
import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/17 上午 11:50
 * @version: 1.0
 */

public class BroadcastPartitioning {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        DataStreamSource<Event> clicks = env.addSource(new ClickSource());

        //广播分区，会出现数据的重复的问题
        //因为经过广播之后，数据会在不同的分区都保留一份，可能进行重复处理。
        // 可以通过调用DataStream的broadcast()方法，将输入数据复制并发送到下游算子的所有并行任务中去。
        clicks.broadcast().print("broadcast").setParallelism(4);

        env.execute();
    }
}
