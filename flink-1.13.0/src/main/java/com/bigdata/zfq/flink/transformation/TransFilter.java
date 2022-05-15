package com.bigdata.zfq.flink.transformation;

import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/15 下午 9:11
 * @version: 1.0
 */

public class TransFilter {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        DataStreamSource<Event> eventDataStreamSource = env.fromElements(new Event("Mary", "./home", 1000L),
                new Event("Bob", "./cart", 2000L)
        );
        //传入匿名类
        SingleOutputStreamOperator<Event> mary = eventDataStreamSource.filter(new FilterFunction<Event>() {
            @Override
            public boolean filter(Event value) throws Exception {
                return value.user.equals("Mary");
            }
        });
        mary.print();
        //匿名函数 lamda表达式
        SingleOutputStreamOperator<Event> mary1 = eventDataStreamSource.filter(data -> data.user.equals("Mary"));
        mary1.print();

        //实现类的方式
        
        eventDataStreamSource.filter(new MyFilter()).print();

        env.execute();
    }

    public static class MyFilter implements FilterFunction<Event> {

        @Override
        public boolean filter(Event value) throws Exception {
            return value.user.equals("Bob");
        }
    }
}
