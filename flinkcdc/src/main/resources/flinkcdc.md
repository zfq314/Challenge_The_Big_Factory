## 1.1 什么是CDC

CDC是Change Data Capture（变更数据获取）的简称。核心思想是，监测并捕获数据库的变动（包括数据或数据表的插入、更新以及删除等），将这些变更按发生的顺序完整记录下来，写入到消息中间件中以供其他服务进行订阅及消费。

## 1.2 CDC的种类

CDC主要分为基于查询和基于Binlog两种方式，我们主要了解一下这两种之间的区别：

|                          | 基于查询的CDC            | 基于Binlog的CDC          |
| ------------------------ | ------------------------ | ------------------------ |
| 开源产品                 | Sqoop、Kafka JDBC Source | Canal、Maxwell、Debezium |
| 执行模式                 | Batch                    | Streaming                |
| 是否可以捕获所有数据变化 | 否                       | 是                       |
| 延迟性                   | 高延迟                   | 低延迟                   |
| 是否增加数据库压力       | 是                       | 否                       |

## 1.3 Flink-CDC

Flink社区开发了 flink-cdc-connectors 组件，这是一个可以直接从 MySQL、PostgreSQL 等数据库直接读取全量数据和增量变更数据的 source 组件。

目前也已开源，开源地址：https://github.com/ververica/flink-cdc-connectors

