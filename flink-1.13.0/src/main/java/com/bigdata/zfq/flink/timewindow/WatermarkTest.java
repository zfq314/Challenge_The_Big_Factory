package com.bigdata.zfq.flink.timewindow;


import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;

//若有延迟数据，无法处理，
//测试数据
//Alice, ./prod?id=300, 1000
//Alice, ./prod?id=300, 10000
//Alice, ./prod?id=300, 15000
//Alice, ./prod?id=300, 9000
//我们就会发现，当最后输入[Alice, ./prod?id=300, 15000]时，流中会周期性地（默认200毫秒）插入一个时间戳为15000L – 5 * 1000L – 1L = 9999毫秒的水位线，已经到达了窗口[0,10000)的结束时间，
//所以会触发窗口的闭合计算。而后面再输入一条[Alice, ./prod?id=200, 9000]时，将不会有任何结果；因为这是一条迟到数据，
//它所属于的窗口已经触发计算然后销毁了（窗口默认被销毁），所以无法再进入到窗口中，自然也就无法更新计算结果了。窗口中的迟到数据默认会被丢弃，这会导致计算结果不够准确。Flink提供了有效处理迟到数据的手段，
public class WatermarkTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.socketTextStream("hadoop31", 7777)
                .map(new MapFunction<String, Event>() {
                    @Override
                    public Event map(String value) throws Exception {
                        String[] split = value.split(",");
                        return new Event(split[0].trim(), split[1].trim(), Long.valueOf(split[2].trim()));
                    }
                })
                //插入水位线的逻辑
                //针对乱序流插入水位线，延迟时间设置5s
                //15000L – 5 * 1000L – 1L = 9999
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(5)).withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                    @Override
                    public long extractTimestamp(Event element, long recordTimestamp) {
                        //抽取时间戳的逻辑
                        return element.timestamp;
                    }
                }))
                //根据user分组，开窗统计
                .keyBy(data -> data.user)
                //开窗
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .process(new WatermarkTestResult())
                .print()
        ;
        env.execute();
    }

    // 自定义处理窗口函数，输出当前的水位线和窗口信息
    public static class WatermarkTestResult extends ProcessWindowFunction<Event, String, String, TimeWindow> {
        @Override
        public void process(String s, Context context, Iterable<Event> elements, Collector<String> out) throws Exception {
            long start = context.window().getStart();
            long end = context.window().getEnd();
            long currentWatermark = context.currentWatermark();
            long exactSizeIfKnown = elements.spliterator().getExactSizeIfKnown();
            out.collect("窗口" + start + " ~ " + end + "中共有" + exactSizeIfKnown + "个元素，窗口闭合计算时，水位线处于：" + currentWatermark);
        }
    }
}
