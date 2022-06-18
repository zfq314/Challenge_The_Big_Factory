package com.bigdata.zfq.flink.timewindow;


import com.bigdata.zfq.flink.source.ClickSource;
import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.HashSet;

//一个电商网站统计每小时UV
//这里我们使用的是事件时间语义。定义10秒钟的滚动事件窗口后，直接使用ProcessWindowFunction来定义处理的逻辑。我们可以创建一个HashSet，将窗口所有数据的userId写入实现去重，最终得到HashSet的元素个数就是UV值。
//当然，这里我们并没有用到上下文中其他信息，所以其实没有必要使用ProcessWindowFunction。全窗口函数因为运行效率较低，很少直接单独使用，往往会和增量聚合函数结合在一起，共同实现窗口的处理计算。


public class UvCountByWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        SingleOutputStreamOperator<Event> eventSingleOutputStreamOperator = env.addSource(new ClickSource())
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO)
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            //指定事件时间
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.timestamp;
                            }
                        }));
        // 将数据全部发往同一分区，按窗口统计UV
        eventSingleOutputStreamOperator.keyBy(data -> true)
                .window(TumblingEventTimeWindows.of(Time.seconds(3)))
                .process(new UvCountByWindows())
                .print();
        env.execute();
    }

    public static class UvCountByWindows extends ProcessWindowFunction<Event, String, Boolean, TimeWindow> {
        @Override
        public void process(Boolean aBoolean, Context context, java.lang.Iterable<Event> elements, Collector<String> out) throws Exception {
            HashSet<Object> set = new HashSet<>();
            // 遍历所有数据，放到Set里去重
            for (Event element : elements) {
                set.add(element.user);
            }
            //结合窗口包装输出内容
            Long start = context.window().getStart();
            Long end = context.window().getEnd();
            out.collect("窗口: " + new Timestamp(start) + " ~ " + new Timestamp(end)
                    + " 的独立访客数量是：" + set.size());

        }
    }
}
