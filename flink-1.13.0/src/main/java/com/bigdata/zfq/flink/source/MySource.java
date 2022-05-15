package com.bigdata.zfq.flink.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/15 下午 4:26
 * @version: 1.0
 */

public class MySource {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        //executionEnvironment.setParallelism(1);
        //从文件中读取数据
        //executionEnvironment.readTextFile("input/click.txt");

        //从集合中读取数据
        ArrayList<Event> event = new ArrayList<>();
        event.add(new Event("Mary", "./home", 1000L));
        event.add(new Event("Bob", "./cart", 2000L));

        //DataStreamSource<Event> eventDataStreamSource = executionEnvironment.fromCollection(event);
        //从Socket读取数据
        // DataStreamSource<String> eventDataStreamSource = executionEnvironment.socketTextStream("hadoop31", 7777);
        //eventDataStreamSource.print();

        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.addSource(new ClickSource()).setParallelism(2);
        // if setParallelism(2)
        // The parallelism of non parallel operator must be 1.
        //如果我们想要自定义并行的数据源的话，需要使用ParallelSourceFunction
        eventDataStreamSource.print();

        executionEnvironment.execute();
    }
}
