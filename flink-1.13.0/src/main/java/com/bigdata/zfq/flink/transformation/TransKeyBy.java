package com.bigdata.zfq.flink.transformation;

import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.stream.Stream;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/15 下午 9:49
 * @version: 1.0
 */

public class TransKeyBy {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Event> stream = env.fromElements(
                new Event("Mary", "./home", 1000L),
                new Event("Bob", "./cart?4", 2000L),
                new Event("Bob", "./cart?3", 3000L),
                new Event("Bob", "./cart?1", 4000L)
        );
        stream.keyBy(data -> data.user).maxBy("timestamp").print("maxby:");
        //maxby:> Event{user='Mary', url='./home', timestamp=1970-01-01 08:00:01.0}
        //max:> Event{user='Mary', url='./home', timestamp=1970-01-01 08:00:01.0}
        //maxby:> Event{user='Bob', url='./cart?4', timestamp=1970-01-01 08:00:02.0}
        //max:> Event{user='Bob', url='./cart?4', timestamp=1970-01-01 08:00:02.0}
        //maxby:> Event{user='Bob', url='./cart?3', timestamp=1970-01-01 08:00:03.0}
        //max:> Event{user='Bob', url='./cart?4', timestamp=1970-01-01 08:00:03.0}
        //maxby:> Event{user='Bob', url='./cart?1', timestamp=1970-01-01 08:00:04.0}
        //max:> Event{user='Bob', url='./cart?4', timestamp=1970-01-01 08:00:04.0}

        //max maxby 针对某个字段max,maxby针对是某条数据

        // 使用匿名类实现KeySelector
        stream.keyBy(new KeySelector<Event, String>() {
            @Override
            public String getKey(Event e) throws Exception {
                return e.user;
            }
        }).max("timestamp").print("max:");

        env.execute();
    }
}
