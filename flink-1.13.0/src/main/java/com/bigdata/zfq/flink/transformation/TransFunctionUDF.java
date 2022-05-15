package com.bigdata.zfq.flink.transformation;

import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/15 下午 10:34
 * @version: 1.0
 */

public class TransFunctionUDF {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        executionEnvironment.setParallelism(1);
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.fromElements(
                new Event("Mary", "./home", 1000L),
                new Event("Bob", "./cart", 2000L)
        );
        SingleOutputStreamOperator<Event> filter = eventDataStreamSource.filter(new FlinkFilter("cart"));
        filter.print();
        executionEnvironment.execute();
    }

    public static class FlinkFilter implements FilterFunction<Event> {
        private String keyWord;

        FlinkFilter(String keyWord) {
            this.keyWord = keyWord;
        }

        @Override
        public boolean filter(Event value) throws Exception {
            return value.url.contains(this.keyWord);
        }
    }
}
