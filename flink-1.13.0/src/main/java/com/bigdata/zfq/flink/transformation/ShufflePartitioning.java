package com.bigdata.zfq.flink.transformation;

import com.bigdata.zfq.flink.source.ClickSource;
import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/16 下午 11:28
 * @version: 1.0
 */

//测试Shuffle分区的数据
public class ShufflePartitioning {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        //读取并行度为1
        DataStreamSource<Event> clicks = env.addSource(new ClickSource());
        // 经洗牌后打印输出，并行度为4
        clicks.shuffle().print("shuffle").setParallelism(4);

        env.execute();
    }
}
