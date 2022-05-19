package com.bigdata.zfq.flink.timewindow;

import com.bigdata.zfq.flink.source.ClickSource;
import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/19 下午 10:04
 * @version: 1.0
 */

public class FlinkWaterMark {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //DataStreamSource<Event> click =
        env.addSource(new ClickSource())
                //插入水位线的逻辑
                .assignTimestampsAndWatermarks
                // 针对乱序流插入水位线，延迟时间设置为5s
                        (WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                                .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.timestamp;
                            }
                        })).print("watermark");
        env.execute();


    }
}
