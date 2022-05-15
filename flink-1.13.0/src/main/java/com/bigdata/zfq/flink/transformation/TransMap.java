package com.bigdata.zfq.flink.transformation;

import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/15 下午 8:45
 * @version: 1.0
 */

public class TransMap {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        DataStreamSource<Event> eventDataStreamSource = env.fromElements(
                new Event("Mary", "./home", 1000L),
                new Event("Bob", "./cart", 2000L)
        );
        //1 传入匿名类，实现MapFunction
        SingleOutputStreamOperator<String> map2 = eventDataStreamSource.map(new MapFunction<Event, String>() {
            @Override
            public String map(Event value) throws Exception {
                return value.user;
            }
        });
        map2.print();
        //2 传入MapFunction的实现类
        SingleOutputStreamOperator<String> map1 = eventDataStreamSource.map(new UserExtractor());
        map1.print();
        //3 传入Lamda表达式
        SingleOutputStreamOperator<String> map = eventDataStreamSource.map(data -> data.user);

        map.print();

        env.execute();
    }

    public static class UserExtractor implements MapFunction<Event, String> {

        @Override
        public String map(Event value) throws Exception {
            return value.user;
        }
    }
}
