package com.bigdata.zfq.flink.sink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;

import java.nio.charset.StandardCharsets;


/**
 * @author: zhaofuqiang
 * @date: 2022/05/18 上午 11:44
 * @version: 1.0
 */

public class SinkCustomtoHBase {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        env.fromElements("hello", "world")
                .addSink(new RichSinkFunction<String>() {
                    // 管理Hbase的配置信息,这里因为Configuration的重名问题，将类以完整路径导入
                    public org.apache.hadoop.conf.Configuration configuration;
                    // 管理Hbase连接
                    public Connection connection;

                    //初始化hbase的连接信息
                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        configuration = HBaseConfiguration.create();
                        configuration.set("hbase.zookeeper.quorum", "hadoop31:2181");
                        //将配置信息填入
                        connection = ConnectionFactory.createConnection(configuration);
                    }
                    //调用信息 主要的逻辑操作


                    @Override
                    public void invoke(String value, Context context) throws Exception {
                        //表名
                        Table table = connection.getTable(TableName.valueOf("click"));
                        //rowkey,并设置字符集
                        Put put = new Put("rowkey".getBytes(StandardCharsets.UTF_8));
                        //指定列名  Column 规则
                        put.addColumn("info".getBytes(StandardCharsets.UTF_8),
                                //写入数据
                                value.getBytes(StandardCharsets.UTF_8),
                                //写入数据
                                "1".getBytes(StandardCharsets.UTF_8));
                        table.put(put);
                        //关闭表
                        table.close();
                    }

                    @Override
                    public void close() throws Exception {
                        super.close();
                        //关闭连接
                        connection.close();
                    }
                });
        env.execute();
    }
}
