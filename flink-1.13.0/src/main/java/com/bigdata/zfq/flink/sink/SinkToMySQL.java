package com.bigdata.zfq.flink.sink;

import com.bigdata.zfq.flink.source.ClickSource;
import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/18 上午 11:19
 * @version: 1.0
 */

public class SinkToMySQL {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Event> click = env.addSource(new ClickSource());
        click.addSink(JdbcSink.sink("insert into clicks (user, url) VALUES (?, ?)",(statement, r)->{
            statement.setString(1,r.user);
            statement.setString(2,r.url);
        },
                JdbcExecutionOptions.builder()
                        .withBatchSize(1000)
                        .withBatchIntervalMs(200)
                        .withMaxRetries(5)
                        .build(),
                //连接信息
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl("jdbc:mysql://hadoop31:3306/ke")
                        .withDriverName("com.mysql.cj.jdbc.Driver")
                        .withUsername("root")
                        .withPassword("hadoopdb-hadooponeoneone@dc.com.")
                        .build()
        ));
        env.execute();
    }
}
