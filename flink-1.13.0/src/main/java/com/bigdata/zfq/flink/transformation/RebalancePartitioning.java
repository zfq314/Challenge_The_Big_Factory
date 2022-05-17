package com.bigdata.zfq.flink.transformation;

import com.bigdata.zfq.flink.source.ClickSource;
import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/17 上午 11:24
 * @version: 1.0
 */

public class RebalancePartitioning {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        DataStreamSource<Event> clicks = env.addSource(new ClickSource());
        //轮询分区
        clicks.rebalance().print("rebalance").setParallelism(4);

        env.execute();
    }
}
