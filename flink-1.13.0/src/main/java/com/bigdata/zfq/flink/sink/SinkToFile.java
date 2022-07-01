package com.bigdata.zfq.flink.sink;

import com.bigdata.zfq.flink.source.ClickSource;
import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.util.concurrent.TimeUnit;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/17 下午 3:02
 * @version: 1.0
 */

public class SinkToFile {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        System.setProperty("HADOOP_USER_NAME","root");
        env.setParallelism(2);
        DataStreamSource<Event> stream = env.addSource(new ClickSource());
        //指定文件生成策略
        StreamingFileSink<String> fileSink = StreamingFileSink.<String>forRowFormat(new Path("hdfs://mycluster/output"), new SimpleStringEncoder<>("UTF-8"))
                //文件滚动策略
                .withRollingPolicy(DefaultRollingPolicy.builder()
                        //	至少包含15分钟的数据
                        //	最近5分钟没有收到新的数据 不活跃时间
                        //	文件大小已达到1 GB 文件大小
                        .withRolloverInterval(TimeUnit.MINUTES.toMillis(15))
                        .withInactivityInterval(TimeUnit.MINUTES.toMillis(5))
                        .withMaxPartSize(1)
                        .build())
                .build();
        stream.map(Event::toString).addSink(fileSink);
        env.execute();
    }
}
