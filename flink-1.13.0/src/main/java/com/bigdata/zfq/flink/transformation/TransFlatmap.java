package com.bigdata.zfq.flink.transformation;

import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/15 下午 9:22
 * @version: 1.0
 */

public class TransFlatmap {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Event> eventDataStreamSource = env.fromElements(new Event("Mary", "./home", 1000L),
                new Event("Bob", "./cart", 2000L)
        );
        eventDataStreamSource.flatMap((Event event, Collector<String> out) -> {
            out.collect(event.user);
            out.collect(event.url);
            out.collect(event.timestamp.toString());
        }).returns(new TypeHint<String>() {
        }).print();
        //TypeHint 类型提示
        eventDataStreamSource.flatMap(new MyFlatMap()).print();

        env.execute();
    }

    //单一抽象方法
    public static class MyFlatMap implements FlatMapFunction<Event, String> {

        @Override
        public void flatMap(Event value, Collector<String> out) throws Exception {
            if (value.user.equals("Mary")) {
                out.collect(value.user);
            } else if (value.user.equals("Bob")) {
                out.collect(value.user);
                out.collect(value.url);
            }
        }
    }
}
