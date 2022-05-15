package com.bigdata.zfq.flink.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

import java.util.Random;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/15 下午 6:13
 * @version: 1.0
 */

public class ParallelSourceExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Integer> integerDataStreamSource = env.addSource(new CustomSource()).setParallelism(4);
        integerDataStreamSource.print();

        env.execute();
    }

    public static class CustomSource implements ParallelSourceFunction<Integer> {
        private Boolean running = true;
        Random random = new Random();

        @Override
        public void run(SourceContext<Integer> ctx) throws Exception {
            while (running) {
                ctx.collect(random.nextInt());
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }
}

