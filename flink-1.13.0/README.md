# zfq-flink-1.13.0 学习

### 第 1 章 初识flink

#### 1.1 Flink 的源起和设计理念

#### 1.2 Flink 的应用

##### 1.2.1 Flink 在企业中的应用（BAT）

##### 1.2.2 Flink 主要的应用场景

###### 行业应用

```
1. 电商和市场营销
举例：实时数据报表、广告投放、实时推荐
2. 物联网（IOT）
举例：传感器实时数据采集和显示、实时报警，交通运输业
3. 物流配送和服务业
举例：订单状态实时更新、通知信息推送
4. 银行和金融业
举例：实时结算和通知推送，实时检测异常行为
```

#### 1.3 流式数据处理的发展和演变

```
stom-sparkstraming-flink
```

#### 1.4 Flink 的特性总结

```
Flink 区别与传统数据处理框架的特性如下。
⚫ 高吞吐和低延迟。每秒处理数百万个事件，毫秒级延迟。
⚫ 结果的准确性。Flink 提供了事件时间（event-time）和处理时间（processing-time）语义。对于乱序事件流，事件时间语义仍然能提供一致且准确的结果。
⚫ 精确一次（exactly-once）的状态一致性保证。
⚫ 可以连接到最常用的存储系统，如 Apache Kafka、Apache Cassandra、Elasticsearch、JDBC、Kinesis 和（分布式）文件系统，如 HDFS 和 S3。 
⚫ 高可用。本身高可用的设置，加上与 K8s，YARN 和 Mesos 的紧密集成，再加上从故障中快速恢复和动态扩展任务的能力，Flink 能做到以极少的停机时间 7×24 全天候运行。
⚫ 能够更新应用程序代码并将作业（jobs）迁移到不同的 Flink 集群，而不会丢失应用程序的状态。

分层 API
最底层级的抽象仅仅提供了有状态流，它将处理函数（Process Function）嵌入到了DataStream API 中
Table API 是以表为中心的声明式编程，其中表在表达流数据时会动态变化。Table API 遵
循关系模型：表有二维数据结构（schema）（类似于关系数据库中的表），同时 API 提供可比
较的操作，例如 select、join、group-by、aggregate 等。
Flink 提供的最高层级的抽象是 SQL。这一层抽象在语法与表达能力上与 Table API 类似，
但是是以 SQL 查询表达式的形式表现程序。SQL 抽象与 Table API 交互密切，同时 SQL 查询
可以直接在 Table API 定义的表上执行。
```

#### 1.5 Flink vs Spark

```
诞生之源：
    Spark 以批处理为根本，并尝试在批处理之上支持流计算；在 Spark 的世界观中，万物皆
    批次，离线数据是一个大批次，而实时数据则是由一个一个无限的小批次组成的。所以对于流
    处理框架 Spark Streaming 而言，其实并不是真正意义上的“流”处理，而是“微批次”
    而 Flink 则认为，流处理才是最基本的操作，批处理也可以统一为流处理。在 Flink 的世
    界观中，万物皆流，实时数据是标准的、没有界限的流，而离线数据则是有界限的流。
数据模型和运行架构
    Spark 底层数据模型是弹性分布式数据集（RDD），Spark Streaming 进行微批处理的底层
    接口 DStream，实际上处理的也是一组组小批数据 RDD 的集合。
    而 Flink 的基本数据模型是数据流（DataFlow），以及事件（Event）序列。
为什么选择flink
	⚫ Flink 的延迟是毫秒级别，而 Spark Streaming 的延迟是秒级延迟。
    ⚫ Flink 提供了严格的精确一次性语义保证。
    ⚫ Flink 的窗口 API 更加灵活、语义更丰富。
    ⚫ Flink 提供事件时间语义，可以正确处理延迟数据。
    ⚫ Flink 提供了更加灵活的对状态编程的 API。
    
```

### 第 2 章 Flink 快速上手

#### 2.1 环境准备

```
    ⚫ 系统环境为 Windows 10。 
    ⚫ 需提前安装 Java 8。 
    ⚫ 集成开发环境（IDE）使用 IntelliJ IDEA，具体的安装流程参见 IntelliJ 官网。
    ⚫ 安装 IntelliJ IDEA 之后，还需要安装一些插件——Maven 和 Git。
```

### 第 3 章 Flink 部署

```
客户端（Client）flink代码程序、
作业管理器（JobManager）
任务管理器（TaskManager）。
    我们的代码，实际上是由客户端获取并做转换，之后提交给JobManger 的。所以 JobManager 就是 Flink 集群里的“管事人”，对作业进行中央调度管理；而它获取到要执行的作业后，会进一步处理转换，然后分发任务给众多的 TaskManager。这里的 TaskManager，就是真正“干活的人”，数据的处理操作都是它们来做的，
```

```
flink-conf.yml
    ⚫ jobmanager.memory.process.size：对 JobManager 进程可使用到的全部内存进行配置，
    包括 JVM 元空间和其他开销，默认为 1600M，可以根据集群规模进行适当调整。
    ⚫ taskmanager.memory.process.size：对 TaskManager 进程可使用到的全部内存进行配置，
    包括 JVM 元空间和其他开销，默认为 1600M，可以根据集群规模进行适当调整。
    ⚫ taskmanager.numberOfTaskSlots：对每个 TaskManager 能够分配的 Slot 数量进行配置，
    默认为 1，可根据 TaskManager 所在的机器能够提供给 Flink 的 CPU 数量决定。所谓
    Slot 就是 TaskManager 中具体运行一个任务所分配的计算资源。
    ⚫ parallelism.default：Flink 任务执行的默认并行度，优先级低于代码中进行的并行度配
    置和任务提交时使用参数指定的并行度数量。
```

#### 3.1 部署模式

```
    ⚫ 会话模式（Session Mode）
    	会话模式其实最符合常规思维。我们需要先启动一个集群，保持一个会话，在这个会话中通过客户端提交作业,集群启动时所有资源就都已经确定，所以所有提交的作业会竞争集群中的资源。
    	会话模式比较适合于单个规模小、执行时间短的大量作业。
    ⚫ 单作业模式（Per-Job Mode）
    	会话模式因为资源共享会导致很多问题，所以为了更好地隔离资源，我们可以考虑为每个提交的作业启动一个集群，这就是所谓的单作业（Per-Job）模式.
    	单作业模式也很好理解，就是严格的一对一，集群只为这个作业而生。同样由客户端运行应用程序，然后启动集群，作业被提交给 JobManager，进而分发给 TaskManager 执行。作业作业完成后，集群就会关闭，所有资源也会释放。这样一来，每个作业都有它自己的 JobManager管理，占用独享的资源，即使发生故障，它的 TaskManager 宕机也不会影响其他作业。这些特性使得单作业模式在生产环境运行更加稳定，所以是实际应用的首选模式。需要注意的是，Flink 本身无法直接这样运行，所以单作业模式一般需要借助一些资源管理框架来启动集群，比如 YARN、Kubernetes。
    ⚫ 应用模式（Application Mode）
        前面提到的两种模式下，应用代码都是在客户端上执行，然后由客户端提交给 JobManager的。但是这种方式客户端需要占用大量网络带宽，去下载依赖和把二进制数据发送给JobManager；加上很多情况下我们提交作业用的是同一个客户端，就会加重客户端所在节点的资源消耗。
        直接把应用提交到 JobManger 上运行。而这也就代表着，我们需要为每一个提交的应用单独启动一个 JobManager，也就是创建一个集群。这个 JobManager 只为执行这一个应用而存在，执行结束之后 JobManager 也就关闭了，这就是所谓的应用模式，
   ⚫ 总结一下
     	在会话模式下，集群的生命周期独立于集群上运行的任何作业的生命周期，并且提交的所有作业共享资源。而单作业模式为每个提交的作业创建一个集群，带来了更好的资源隔离，这时集群的生命周期与作业的生命周期绑定。最后，应用模式为每个应用程序创建一个会话集群，在 JobManager 上直接调用应用程序的 main()方法。    
        
```

#### 3.2 独立模式（Standalone）

```
独立模式（Standalone）是部署 Flink 最基本也是最简单的方式：所需要的所有 Flink 组件，都只是操作系统上运行的一个 JVM 进程。独立模式是独立运行的，不依赖任何外部的资源管理平台；当然独立也是有代价的：如果资源不足，或者出现故障，没有自动扩展或重分配资源的保证，必须手动处理。所以独立模式一般只用在开发测试或作业非常少的场景下。
(会话模式部署)支持独立部署模式
Flink 的独立（Standalone）集群并不支持单作业模式部署
```

#### 3.3 YARN 模式

```
YARN 的会话模式与独立集群略有不同，需要首先申请一个 YARN 会话（YARN session）来启动 Flink 集群。高版本的flink配置hadoop的环境变量来实现
 HADOOP_HOME=/opt/module/hadoop-2.7.5
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
export HADOOP_CONF_DIR=${HADOOP_HOME}/etc/hadoop
export HADOOP_CLASSPATH=`hadoop classpath`
 bin/yarn-session.sh -nm test
 会话模式
 $ bin/flink run-c com.zfq.wc.StreamWordCount FlinkTutorial-1.0-SNAPSHOT.jar
 
```

### 第5章 DataStream API（基础篇）

#### 5.1 执行环境（Execution Environment）

```
5.1.1 创建执行环境
1. getExecutionEnvironment 自适应的自动获取的一个环境 
2. createLocalEnvironment
3. createRemoteEnvironment 

5.1.2 执行模式(Execution Mode)
	批执行模式（BATCH）
专门用于批处理的执行模式, 这种模式下，Flink处理作业的方式类似于MapReduce框架。对于不会持续计算的有界数据，我们用这种模式处理会更方便。
	自动模式（AUTOMATIC）
在这种模式下，将由程序根据输入数据源是否有界，来自动选择执行模式。
1. BATCH模式的配置方法
由于Flink程序默认是STREAMING模式，我们这里重点介绍一下BATCH模式的配置。主要有两种方式：
（1）通过命令行配置
bin/flink run -Dexecution.runtime-mode=BATCH ...
在提交作业时，增加execution.runtime-mode参数，指定值为BATCH。
（2）通过代码配置
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
env.setRuntimeMode(RuntimeExecutionMode.BATCH);
在代码中，直接基于执行环境调用setRuntimeMode方法，传入BATCH模式。
建议: 不要在代码中配置，而是使用命令行。这同设置并行度是类似的：在提交作业时指定参数可以更加灵活，同一段应用程序写好之后，既可以用于批处理也可以用于流处理。而在代码中硬编码（hard code）的方式可扩展性比较差，一般都不推荐。

5.1.3 触发程序执行

有了执行环境，我们就可以构建程序的处理流程了：基于环境读取数据源，进而进行各种转换操作，最后输出结果到外部系统。
需要注意的是，写完输出（sink）操作并不代表程序已经结束。因为当main()方法被调用时，其实只是定义了作业的每个执行操作，然后添加到数据流图中；这时并没有真正处理数据——因为数据可能还没来。Flink是由事件驱动的，只有等到数据到来，才会触发真正的计算，这也被称为“延迟执行”或“懒执行”（lazy execution）。
所以我们需要显式地调用执行环境的execute()方法，来触发程序执行。execute()方法将一直等待作业完成，然后返回一个执行结果（JobExecutionResult）。
env.execute();

```

