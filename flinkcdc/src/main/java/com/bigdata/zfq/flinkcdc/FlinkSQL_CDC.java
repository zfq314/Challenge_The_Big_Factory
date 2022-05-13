package com.bigdata.zfq.flinkcdc;


import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/12 下午 9:56
 * @version: 1.0
 */

public class FlinkSQL_CDC {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env);

        //创建动态表
        tableEnvironment.executeSql("create table qrtz_scheduler_state (" +
                "SCHED_NAME STRING NOT NULL," +
                "INSTANCE_NAME STRING NOT NULL," +
                "LAST_CHECKIN_TIME BIGINT NOT NULL," +
                "CHECKIN_INTERVAL BIGINT NOT NULL," +
                " PRIMARY KEY (SCHED_NAME,INSTANCE_NAME) NOT ENFORCED" +
                " ) WITH (" +
                "  'connector' = 'mysql-cdc'," +
                "  'hostname' = 'hadoop31'," +
                "  'port' = '3306'," +
                "  'username' = 'root'," +
                "  'password' = 'hadoopdb-hadooponeoneone@dc.com.'," +
                "  'database-name' = 'dolphinscheduler'," +
                "  'table-name' = 'qrtz_scheduler_state'" +
                ")");
        tableEnvironment.executeSql("select * from qrtz_scheduler_state").print();

        env.execute();
    }
}
