package com.bigdata.zfq.flink.timewindow;


import com.bigdata.zfq.flink.source.ClickSource;
import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.HashSet;
//代码中我们创建了事件时间滑动窗口，统计10秒钟的“人均PV”，每2秒统计一次。由于聚合的状态还需要做处理计算，因此窗口聚合时使用了更加灵活的AggregateFunction。
// 为了统计UV，我们用一个HashSet保存所有出现过的用户id，实现自动去重；而PV的统计则类似一个计数器，每来一个数据加一就可以了。
// 所以这里的状态，定义为包含一个HashSet和一个count值的二元组（Tuple2<HashSet<String>, Long>），每来一条数据，就将user存入HashSet，同时count加1。
// 这里的count就是PV，而HashSet中元素的个数（size）就是UV；所以最终窗口的输出结果，就是它们的比值。
//这里没有涉及会话窗口，所以merge()方法可以不做任何操作。


//聚合函数的例子
public class WindowAggregateFunction {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        SingleOutputStreamOperator<Event> eventSingleOutputStreamOperator = env.addSource(new ClickSource())
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forMonotonousTimestamps().withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                    @Override
                    public long extractTimestamp(Event element, long recordTimestamp) {
                        return element.timestamp;
                    }
                }));

        // 所有数据设置相同的key，发送到同一个分区统计PV和UV，再相除
        eventSingleOutputStreamOperator.keyBy(data -> true).window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(2))).aggregate(new AvgPv()).print();
        env.execute();
    }


    private static class AvgPv implements AggregateFunction<Event, Tuple2<HashSet<String>, Long>, Double> {
        //创建累加器
        @Override
        public Tuple2<HashSet<String>, Long> createAccumulator() {
            return Tuple2.of(new HashSet<String>(), 0L);
        }

        //属于窗口的数据来一次累加一次，并返回累加器
        @Override
        public Tuple2<HashSet<String>, Long> add(Event value, Tuple2<HashSet<String>, Long> accumulator) {
            accumulator.f0.add(value.user);
            return Tuple2.of(accumulator.f0, accumulator.f1 + 1L);
        }

        // 窗口闭合时，增量聚合结束，将计算结果发送到下游
        @Override
        public Double getResult(Tuple2<HashSet<String>, Long> accumulator) {
            return (double) accumulator.f1 / accumulator.f0.size();
        }

        @Override
        public Tuple2<HashSet<String>, Long> merge(Tuple2<HashSet<String>, Long> a, Tuple2<HashSet<String>, Long> b) {
            return null;
        }
    }
}
