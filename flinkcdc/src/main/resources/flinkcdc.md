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

## 1.4 Flink StateBackend 状态后端

一、概述

保存机制 StateBackend ，默认情况下，State 会保存在 TaskManager 的内存中，CheckPoint 会存储在 JobManager 的内存中。

State 和 CheckPoint 的存储位置取决于 StateBackend 的配置。

Flink 一共提供了 3 中 StateBackend，包括

　　基于内存的 MemoryStateBackend、基于文件系统的 FsStateBackend、基于RockDB存储介质的 RocksDBState-Backend

1）MemoryStateBackend

　　基于内存的状态管理，具有非常快速和高效的特点，但也具有非常多的限制。最主要的就是内存的容量限制，

一旦存储的状态数据过多就会导致系统内存溢出等问题，从而影响整个应用的正常运行。同时如果机器出现问题，

整个主机内存中的状态数据都会丢失，进而无法恢复任务中的状态数据。因此从数据安全的角度建议用户尽可能地避免

在生产环境中使用 MemoryStateBackend

```
streamEnv.setStateBackend(new MemoryStateBackend(10*1024*1024))
```

2）FsStateBackend

　　和MemoryStateBackend有所不同，FsStateBackend是基于文件系统的一种状态管理器，这里的文件系统可以是本地文件系统，

也可以是HDFS分布式文件 系统。FsStateBackend更适合任务状态非常大的情况，例如：应用中含有时间范围非常长的窗口计算，

或 Key/Value State 状态数据量非常大的场景。

　　streamEnv.setStateBackend(new FsStateBackend("hdfs://hadoop101:9000/checkpoint/cp1"))

3）RocksDBStatusBackend

　　RocksDBStatusBackend是Flink中内置的第三方状态管理器，和前面的状态管理器不同，RocksDBStateBackend 需要单独引入

相关的依赖包到工程中。

```
<dependency>
　　<groupId>org.apache.flink</groupId>
　　<artifactId>flink-statebackend-rocksdb_2.11</artifactId>
　　<version>1.9.1</version>
</dependency>
```

　　RocksDBStateBackend 采用异步的方式进行状态数据的 Snapshot，任务中的状态数据首先被写入本地 RockDB 中，这样在

RockDB 仅会存储正在计算的热数据，而需要进行 CheckPoint 的时候，会把本地的数据直接复制到远端的 FileSystem 中。

　　与 FsStateBackend 相比，RocksDBStateBackend 在性能上要比 FsStateBackend 高一些，主要是因为借助于 RocksDB 在

本地存储了最新热数据，然后通过异步的方式再同步到文件系统中，但 RocksDBStateBackend 和 MemoryStateBackend 相比性能

就会较弱一些。RocksDB 克服了 State 受内存限制的缺点，同时又能够持久化到远端文件系统中，推荐在生产中使用。

```
streamEnv.setStateBackend(new RocksDBStateBackend("hdfs://hadoop101:9000/checkpoint/cp2"))
```

4）全局配置 StateBackend

　　前面的几个代码都是单 job 配置状态后端，也可以全局配置状态后端，需要修改 flink-conf.yaml配置文件：

```
state.backend:filesystem
```

　　其中：

　　filesystem 表示使用 FsStateBackend

　　jobmanager 表示使用 MemoryStateBackend

　　rocksdb 表示使用 RocksDBStateBackend

```
state.checkpoint.dir:hdfs://hadoop101:9000/checkpoints
```

　　默认情况下，如果设置了 Checkpoint 选项，则 Flink 只保留最近成功生成的 1 个 Checkpoint，而当 Flink 程序失败时，可以通过最近的 CheckPoint 来进行恢复。但是希望保留多个 CheclPoint，并能够根据实际需要选择其中一个进行恢复，就会更加灵活。添加如下配置，指定最多可以保存的 CheckPoint 的个数

 state.checkpoints.num-retained: 2
