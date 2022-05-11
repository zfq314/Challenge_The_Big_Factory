package com.bigdata.zfq.flinkcdc;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment;

/**
 * TODO
 *
 * @Author zhaofuqiang
 * @Date 2022/05/11 下午 9:22
 */

public class FlinkCDC {
    public static void main(String[] args) {
        //执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //开启checkpoint或者savepoint
        env.enableCheckpointing(5000L);
        //指定CK的一致性语义
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //设置任务关闭的时候保留最后一次CK数据
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //指定从CK自动重启策略
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 2000L));
        //设置状态后端
        env.setStateBackend(new FsStateBackend("hdfs://hadoop102:8020/flinkCDC"));
        //设置访问HDFS的用户名
        System.setProperty("HADOOP_USER_NAME", "root");

        //创建Flink-MySQL-CDC的Source

    }
}
