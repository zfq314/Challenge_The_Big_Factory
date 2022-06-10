package com.bigdata.zfq.flink.timewindow;

import com.bigdata.zfq.flink.source.ClickSource;
import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.Duration;

// 代码中我们对每个用户的行为数据进行了开窗统计。与word count逻辑类似，首先将数据转换成(user, count)的二元组形式（类型为Tuple2<String, Long>），
// 每条数据对应的初始count值都是1；然后按照用户id分组，在处理时间下开滚动窗口，统计每5秒内的用户行为数量。对于窗口的计算，
// 我们用ReduceFunction对count值做了增量聚合：窗口中会将当前的总count值保存成一个归约状态，每来一条数据，就会调用内部的reduce方法，将新数据中的count值叠加到状态上，
// 并得到新的状态保存起来。等到了5秒窗口的结束时间，就把归约好的状态直接输出。
// 这里需要注意，我们经过窗口聚合转换输出的数据，数据类型依然是二元组Tuple2<String, Long>。

//创建规约操作例子
public class WindowReduceExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);

        //自定义数据源
        SingleOutputStreamOperator<Event> stream = executionEnvironment.addSource(new ClickSource())
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO)//watermark
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.timestamp;
                            }
                        }));
        stream.map(new MapFunction<Event, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(Event value) throws Exception {
                //将数据转化成二元组,便于计算
                return Tuple2.of(value.user, 1L);
            }
        }).keyBy(r -> r.f0)
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))//设置滚动事件时间窗口
                .reduce(new ReduceFunction<Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> reduce(Tuple2<String, Long> value1, Tuple2<String, Long> value2) throws Exception {
                        // 定义累加规则，窗口闭合时，向下游发送累加结果
                        return Tuple2.of(value1.f0, value1.f1 + value2.f1);
                    }
                }).print()
        ;


        executionEnvironment.execute();
    }
}
