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

### 第4章 Flink运行时架构

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

#### 5.2 源算子（Source）

```
5.2.7  Flink支持的数据类型
我们已经了解了Flink怎样从不同的来源读取数据。在之前的代码中，我们的数据都是定义好的UserBehavior类型，而且在5.2.1小节中特意说明了对这个类的要求。那还有没有其他更灵活的类型可以用呢？Flink支持的数据类型到底有哪些？

1. Flink的类型系统
   为什么会出现“不支持”的数据类型呢？因为Flink作为一个分布式处理框架，处理的是以数据对象作为元素的流。如果用水流来类比，那么我们要处理的数据元素就是随着水流漂动的物体。在这条流动的河里，可能漂浮着小木块，也可能行驶着内部错综复杂的大船。要分布式地处理这些数据，就不可避免地要面对数据的网络传输、状态的落盘和故障恢复等问题，这就需要对数据进行序列化和反序列化。小木块是容易序列化的；而大船想要序列化之后传输，就需要将它拆解、清晰地知道其中每一个零件的类型。
   为了方便地处理数据，Flink有自己一整套类型系统。Flink使用“类型信息”（TypeInformation）来统一表示数据类型。TypeInformation类是Flink中所有类型描述符的基类。它涵盖了类型的一些基本属性，并为每个数据类型生成特定的序列化器、反序列化器和比较器。
2. Flink支持的数据类型
   简单来说，对于常见的Java和Scala数据类型，Flink都是支持的。Flink在内部，Flink对支持不同的类型进行了划分，这些类型可以在Types工具类中找到：
   （1）基本类型
   所有Java基本类型及其包装类，再加上Void、String、Date、BigDecimal和BigInteger。
   （2）数组类型
   包括基本类型数组（PRIMITIVE_ARRAY）和对象数组(OBJECT_ARRAY)
   （3）复合数据类型
   	Java元组类型（TUPLE）：这是Flink内置的元组类型，是Java API的一部分。最多25个字段，也就是从Tuple0~Tuple25，不支持空字段
   	Scala 样例类及Scala元组：不支持空字段
   	行类型（ROW）：可以认为是具有任意个字段的元组,并支持空字段
   	POJO：Flink自定义的类似于Java bean模式的类
   （4）辅助类型
   Option、Either、List、Map等
   （5）泛型类型（GENERIC）
   Flink支持所有的Java类和Scala类。不过如果没有按照上面POJO类型的要求来定义，就会被Flink当作泛型类来处理。Flink会把泛型类型当作黑盒，无法获取它们内部的属性；它们也不是由Flink本身序列化的，而是由Kryo序列化的。
   在这些类型中，元组类型和POJO类型最为灵活，因为它们支持创建复杂类型。而相比之下，POJO还支持在键（key）的定义中直接使用字段名，这会让我们的代码可读性大大增加。所以，在项目实践中，往往会将流处理程序中的元素类型定为Flink的POJO类型。
   Flink对POJO类型的要求如下：
   	类是公共的（public）和独立的（standalone，也就是说没有非静态的内部类）；
   	类有一个公共的无参构造方法；
   	类中的所有字段是public且非final的；或者有一个公共的getter和setter方法，这些方法需要符合Java bean的命名规范。
   所以我们看到，之前的UserBehavior，就是我们创建的符合Flink POJO定义的数据类型。
3. 类型提示（Type Hints）
   Flink还具有一个类型提取系统，可以分析函数的输入和返回类型，自动获取类型信息，从而获得对应的序列化器和反序列化器。但是，由于Java中泛型擦除的存在，在某些特殊情况下（比如Lambda表达式中），自动提取的信息是不够精细的——只告诉Flink当前的元素由“船头、船身、船尾”构成，根本无法重建出“大船”的模样；这时就需要显式地提供类型信息，才能使应用程序正常工作或提高其性能。
   为了解决这类问题，Java API提供了专门的“类型提示”（type hints）。
   回忆一下之前的word count流处理程序，我们在将String类型的每个词转换成（word， count）二元组后，就明确地用returns指定了返回的类型。因为对于map里传入的Lambda表达式，系统只能推断出返回的是Tuple2类型，而无法得到Tuple2<String, Long>。只有显式地告诉系统当前的返回类型，才能正确地解析出完整数据。
   .map(word -> Tuple2.of(word, 1L))
   .returns(Types.TUPLE(Types.STRING, Types.LONG));
   这是一种比较简单的场景，二元组的两个元素都是基本数据类型。那如果元组中的一个元素又有泛型，该怎么处理呢？
   Flink专门提供了TypeHint类，它可以捕获泛型的类型信息，并且一直记录下来，为运行时提供足够的信息。我们同样可以通过.returns()方法，明确地指定转换之后的DataStream里元素的类型。
```

```
如果source为外面的数据源，需要相应的连接器
```

#### 5.3 转换算子（Transformation）

```
所以在Flink中，要做聚合，需要先进行分区；这个操作就是通过keyBy来完成的。

keyBy是聚合前必须要用到的一个算子。keyBy通过指定键（key），可以将一条流从逻辑上划分成不同的分区（partitions）。这里所说的分区，其实就是并行处理的子任务，也就对应着任务槽（task slot）。

在内部，是通过计算key的哈希值（hash code），对分区数进行取模运算来实现的。所以这里key如果是POJO的话，必须要重写hashCode()方法。
keyBy()方法需要传入一个参数，这个参数指定了一个或一组key。有很多不同的方法来指定key：比如对于Tuple数据类型，可以指定字段的位置或者多个位置的组合；对于POJO类型，可以指定字段的名称（String）；另外，还可以传入Lambda表达式或者实现一个键选择器（KeySelector），用于说明从数据中提取key的逻辑。

简单聚合
有了按键分区的数据流KeyedStream，我们就可以基于它进行聚合操作了。Flink为我们内置实现了一些最基本、最简单的聚合API，主要有以下几种：
	sum()：在输入流上，对指定的字段做叠加求和的操作。
	min()：在输入流上，对指定的字段求最小值。
	max()：在输入流上，对指定的字段求最大值。
	minBy()：与min()类似，在输入流上针对指定字段求最小值。不同的是，min()只计算指定字段的最小值，其他字段会保留最初第一个数据的值；而minBy()则会返回包含字段最小值的整条数据。
	maxBy()：与max()类似，在输入流上针对指定字段求最大值。两者区别与min()/minBy()完全一致。
```

```
自定义实现函数

1. 函数类（Function Classes）
对于大部分操作而言，都需要传入一个用户自定义函数（UDF），实现相关操作的接口，来完成处理逻辑的定义。Flink暴露了所有UDF函数的接口，具体实现方式为接口或者抽象类，例如MapFunction、FilterFunction、ReduceFunction等。
所以最简单直接的方式，就是自定义一个函数类，实现对应的接口。之前我们对于API的练习，主要就是基于这种方式。

匿名函数（Lambda）
匿名函数（Lambda表达式）是Java 8 引入的新特性，方便我们更加快速清晰地写代码。 Lambda 表达式允许以简洁的方式实现函数，以及将函数作为参数来进行传递，而不必声明额外的（匿名）类。
Flink 的所有算子都可以使用 Lambda 表达式的方式来进行编码，但是，当 Lambda 表达式使用 Java 的泛型时，我们需要显式的声明类型信息。


泛型信息擦除掉了。这样 Flink 就无法自动推断输出的类型信息了。
解决方案
1 // 想要转换成二元组类型，需要进行以下处理
        // 1) 使用显式的 ".returns(...)"
        DataStream<Tuple2<String, Long>> stream3 = clicks
                .map( event -> Tuple2.of(event.user, 1L) )
                .returns(Types.TUPLE(Types.STRING, Types.LONG));
        stream3.print();
2) // 使用类来替代Lambda表达式
        clicks.map(new MyTuple2Mapper())
                .print();
3) //  使用匿名类来代替Lambda表达式
        clicks.map(new MapFunction<Event, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(Event value) throws Exception {
                return Tuple2.of(value.user, 1L);
            }
        }).print();
4) // 自定义MapFunction的实现类
    public static class MyTuple2Mapper implements MapFunction<Event, Tuple2<String, Long>>{
        @Override
        public Tuple2<String, Long> map(Event value) throws Exception {
            return Tuple2.of(value.user, 1L);
        }

```

```
Rich Function有生命周期的概念。典型的生命周期方法有：
	open()方法，是Rich Function的初始化方法，也就是会开启一个算子的生命周期。当一个算子的实际工作方法例如map()或者filter()方法被调用之前，open()会首先被调用。所以像文件IO的创建，数据库连接的创建，配置文件的读取等等这样一次性的工作，都适合在open()方法中完成。。
	close()方法，是生命周期中的最后一个调用的方法，类似于解构方法。一般用来做一些清理工作。
```

```
5.3.4 物理分区（Physical Partitioning）
keyBy这种逻辑分区是一种“软分区”，那真正硬核的分区就应该是所谓的“物理分区”（physical partitioning）。也就是我们要真正控制分区策略，精准地调配数据，告诉每个数据到底去哪里

为了同keyBy相区别，我们把这些操作统称为“物理分区”操作。物理分区与keyBy另一大区别在于，keyBy之后得到的是一个KeyedStream，而物理分区之后结果仍是DataStream，且流中元素数据类型保持不变。从这一点也可以看出，分区算子并不对数据进行转换处理，只是定义了数据的传输方式。
常见的物理分区策略有随机分配（Random）、轮询分配（Round-Robin）、重缩放（Rescale）和广播（Broadcast），下边我们分别来做了解。

1. 随机分区（shuffle）
最简单的重分区方式就是直接“洗牌”。通过调用DataStream的.shuffle()方法，将数据随机地分配到下游算子的并行任务中去。
随机分区服从均匀分布（uniform distribution），所以可以把流中的数据随机打乱，均匀地传递到下游任务分区

2. 轮询分区（Round-Robin）
轮询也是一种常见的重分区方式。简单来说就是“发牌”，按照先后顺序将数据做依次分发
通过调用DataStream的.rebalance()方法，就可以实现轮询重分区。rebalance使用的是Round-Robin负载均衡算法，可以将输入流数据平均分配到下游的并行任务中去。
注：Round-Robin算法用在了很多地方，例如Kafka和Nginx。

3. 重缩放分区（rescale）
重缩放分区和轮询分区非常相似。当调用rescale()方法时，其实底层也是使用Round-Robin算法进行轮询，但是只会将数据轮询发送到下游并行任务的一部分中，也就是说，“发牌人”如果有多个，那么rebalance的方式是每个发牌人都面向所有人发牌；而rescale的做法是分成小团体，发牌人只给自己团体内的所有人轮流发牌。

当下游任务（数据接收方）的数量是上游任务（数据发送方）数量的整数倍时，rescale的效率明显会更高。比如当上游任务数量是2，下游任务数量是6时，上游任务其中一个分区的数据就将会平均分配到下游任务的3个分区中。 
由于rebalance是所有分区数据的“重新平衡”，当TaskManager数据量较多时，这种跨节点的网络传输必然影响效率；而如果我们配置的task slot数量合适，用rescale的方式进行“局部重缩放”，就可以让数据只在当前TaskManager的多个slot之间重新分配，从而避免了网络传输带来的损耗。
从底层实现上看，rebalance和rescale的根本区别在于任务之间的连接机制不同。rebalance将会针对所有上游任务（发送数据方）和所有下游任务（接收数据方）之间建立通信通道，这是一个笛卡尔积的关系；而rescale仅仅针对每一个任务和下游对应的部分任务之间建立通信通道，节省了很多资源。

4. 广播（broadcast）
这种方式其实不应该叫做“重分区”，因为经过广播之后，数据会在不同的分区都保留一份，可能进行重复处理。可以通过调用DataStream的broadcast()方法，将输入数据复制并发送到下游算子的所有并行任务中去。

5. 全局分区（global）
全局分区也是一种特殊的分区方式。这种做法非常极端，通过调用.global()方法，会将所有的输入流数据都发送到下游算子的第一个并行子任务中去。这就相当于强行让下游任务并行度变成了1，所以使用这个操作需要非常谨慎，可能对程序造成很大的压力。

6. 自定义分区（Custom）
当Flink提供的所有分区策略都不能满足用户的需求时，我们可以通过使用partitionCustom()方法来自定义分区策略。
在调用时，方法需要传入两个参数，第一个是自定义分区器（Partitioner）对象，第二个是应用分区器的字段，它的指定方式与keyBy指定key基本一样：可以通过字段名称指定，也可以通过字段位置索引来指定，还可以实现一个KeySelector。


```

### flink 和 spark的通信协议

```
Flink内部节点之间的通信是用Akka，比如JobManager和TaskManager之间的通信。而operator之间的数据传输是利用Netty。

Spark用Netty通信框架代替Akka
```

#### 5.4 输出算子（Sink）

```
addSink传入的参数是一个FlinkKafkaProducer。这也很好理解，因为需要向Kafka写入数据，自然应该创建一个生产者。FlinkKafkaProducer继承了抽象类TwoPhaseCommitSinkFunction，这是一个实现了“两阶段提交”的RichSinkFunction。两阶段提交提供了Flink向Kafka写入数据的事务性保证，能够真正做到精确一次（exactly once）的状态一致性。
```

```
sink redis
启动redis：redis-server redis.conf 
进入客户端：redis-cli
Flink没有直接提供官方的Redis连接器，不过Bahir项目还是担任了合格的辅助角色，为我们提供了Flink-Redis的连接工具。但版本升级略显滞后，目前连接器版本为1.0，支持的Scala版本最新到2.11。由于我们的测试不涉及到Scala的相关版本变化，所以并不影响使用。在实际项目应用中，应该以匹配的组件版本运行。
```

### 第6章 Flink中的时间和窗口

#### 6.1 时间语义

##### 6.1.1 Flink中的时间语义

```
1. 处理时间（Processing Time）
处理时间的概念非常简单，就是指执行处理操作的机器的系统时间。
2. 事件时间（Event Time）
事件时间，是指每个事件在对应的设备上发生的时间，也就是数据生成的时间。
数据一旦产生，这个时间自然就确定了，所以它可以作为一个属性嵌入到数据中。这其实就是这条数据记录的“时间戳”（Timestamp）。
所以在实际应用中，事件时间语义会更为常见。一般情况下，业务日志数据中都会记录数据生成的时间戳（timestamp），它就可以作为事件时间的判断基础。

另外，除了事件时间和处理时间，Flink还有一个“摄入时间”（Ingestion Time）的概念，它是指数据进入Flink数据流的时间，也就是Source算子读入数据的时间。摄入时间相当于是事件时间和处理时间的一个中和，它是把Source任务的处理时间，当作了数据的产生时间添加到数据里。这样一来，水位线（watermark）也就基于这个时间直接生成，不需要单独指定了。这种时间语义可以保证比较好的正确性，同时又不会引入太大的延迟。它的具体行为跟事件时间非常像，可以当作特殊的事件时间来处理。

在Flink中，由于处理时间比较简单，早期版本默认的时间语义是处理时间；而考虑到事件时间在实际应用中更为广泛，从1.12版本开始，Flink已经将事件时间作为了默认的时间语义。
```

#### 6.2 水位线（Watermark）

```
在介绍事件时间语义时，我们提到了“水位线”的概念，已经知道了它其实就是用来度量事件时间的。那么水位线具体有什么含义，又跟数据的时间戳有什么关系呢？接下来我们就来深入探讨一下这个流处理中的核心概念。

在实际应用中，一般会采用事件时间语义。而水位线，就是基于事件时间提出的概念。所以在介绍水位线之前，我们首先来梳理一下事件时间和窗口的关系。

```

##### 6.2.2 什么是水位线

```
在事件时间语义下，我们不依赖系统时间，而是基于数据自带的时间戳去定义了一个时钟，用来表示当前时间的进展。于是每个并行子任务都会有一个自己的逻辑时钟，它的前进是靠数据的时间戳来驱动的。

但在分布式系统中，这种驱动方式又会有一些问题。因为数据本身在处理转换的过程中会变化，如果遇到窗口聚合这样的操作，其实是要攒一批数据才会输出一个结果，那么下游的数据就会变少，时间进度的控制就不够精细了。另外，数据向下游任务传递时，一般只能传输给一个子任务（除广播外），这样其他的并行子任务的时钟就无法推进了。
一种简单的想法是，在数据流中加入一个时钟标记，记录当前的事件时间；这个标记可以直接广播到下游，当下游任务收到这个标记，就可以更新自己的时钟了。由于类似于水流中用来做标志的记号，在Flink中，这种用来衡量事件时间（Event Time）进展的标记，就被称作“水位线”（Watermark）。

1. 有序流中的水位线
在理想状态下，数据应该按照它们生成的先后顺序、排好队进入流中；也就是说，它们处理的过程会保持原先的顺序不变，遵守先来后到的原则。这样的话我们从每个数据中提取时间戳，就可以保证总是从小到大增长的，从而插入的水位线也会不断增长、事件时钟不断向前推进。
实际应用中，如果当前数据量非常大，可能会有很多数据的时间戳是相同的，这时每来一条数据就提取时间戳、插入水位线就做了大量的无用功。而且即使时间戳不同，同时涌来的数据时间差会非常小（比如几毫秒），往往对处理计算也没什么影响。所以为了提高效率，一般会每隔一段时间生成一个水位线，这个水位线的时间戳，就是当前最新数据的时间戳

所以对于水位线的周期性生成，周期时间是指处理时间（系统时间），而不是事件时间。

2. 乱序流中的水位线
有序流的处理非常简单，看起来水位线也并没有起到太大的作用。但这种情况只存在于理想状态下。我们知道在分布式系统中，数据在节点间传输，会因为网络传输延迟的不确定性，导致顺序发生改变，这就是所谓的“乱序数据”。
这里所说的“乱序”（out-of-order），是指数据的先后顺序不一致，主要就是基于数据的产生时间而言的
我们插入新的水位线时，要先判断一下时间戳是否比之前的大，否则就不再生成新的水位线，


3. 水位线的特性
现在我们可以知道，水位线就代表了当前的事件时间时钟，而且可以在数据的时间戳基础上加一些延迟来保证不丢数据，这一点对于乱序流的正确处理非常重要。
我们可以总结一下水位线的特性：
	水位线是插入到数据流中的一个标记，可以认为是一个特殊的数据
	水位线主要的内容是一个时间戳，用来表示当前事件时间的进展
	水位线是基于数据的时间戳生成的
	水位线的时间戳必须单调递增，以确保任务的事件时间时钟一直向前推进
	水位线可以通过设置延迟，来保证正确处理乱序数据
	一个水位线Watermark(t)，表示在当前流中事件时间已经达到了时间戳t, 这代表t之前的所有数据都到齐了，之后流中不会出现时间戳t’ ≤ t的数据
水位线是Flink流处理中保证结果正确性的核心机制，它往往会跟窗口一起配合，完成对乱序数据的正确处理。

```

##### 6.2.3 如何生成水位线

```
2. 水位线生成策略（Watermark Strategies）
在Flink的DataStream API中，有一个单独用于生成水位线的方法：.assignTimestampsAndWatermarks()，它主要用来为流中的数据分配时间戳，并生成水位线来指示事件时间：
public SingleOutputStreamOperator<T> assignTimestampsAndWatermarks(
        WatermarkStrategy<T> watermarkStrategy)
具体使用时，直接用DataStream调用该方法即可，与普通的transform方法完全一样。
DataStream<Event> stream = env.addSource(new ClickSource());
DataStream<Event> withTimestampsAndWatermarks = 
stream.assignTimestampsAndWatermarks(<watermark strategy>);
这里读者可能有疑惑：不是说数据里已经有时间戳了吗，为什么这里还要“分配”呢？这是因为原始的时间戳只是写入日志数据的一个字段，如果不提取出来并明确把它分配给数据，Flink是无法知道数据真正产生的时间的。当然，有些时候数据源本身就提供了时间戳信息，比如读取Kafka时，我们就可以从Kafka数据中直接获取时间戳，而不需要单独提取字段分配了。
.assignTimestampsAndWatermarks()方法需要传入一个WatermarkStrategy作为参数，这就是所谓的“水位线生成策略”。WatermarkStrategy中包含了一个“时间戳分配器”TimestampAssigner和一个“水位线生成器”WatermarkGenerator。
public interface WatermarkStrategy<T> 
    extends TimestampAssignerSupplier<T>,
            WatermarkGeneratorSupplier<T>{

    @Override
    TimestampAssigner<T> createTimestampAssigner(TimestampAssignerSupplier.Context context);

    @Override
    WatermarkGenerator<T> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context);
}
	TimestampAssigner：主要负责从流中数据元素的某个字段中提取时间戳，并分配给元素。时间戳的分配是生成水位线的基础。
	WatermarkGenerator：主要负责按照既定的方式，基于时间戳生成水位线。在WatermarkGenerator接口中，主要又有两个方法：onEvent()和onPeriodicEmit()。
	onEvent：每个事件（数据）到来都会调用的方法，它的参数有当前事件、时间戳，以及允许发出水位线的一个WatermarkOutput，可以基于事件做各种操作
	onPeriodicEmit：周期性调用的方法，可以由WatermarkOutput发出水位线。周期时间为处理时间，可以调用环境配置的.setAutoWatermarkInterval()方法来设置，默认为200ms。
env.getConfig().setAutoWatermarkInterval(60 * 1000L);


3. Flink内置水位线生成器
WatermarkStrategy这个接口是一个生成水位线策略的抽象，让我们可以灵活地实现自己的需求；但看起来有些复杂，如果想要自己实现应该还是比较麻烦的。好在Flink充分考虑到了我们的痛苦，提供了内置的水位线生成器（WatermarkGenerator），不仅开箱即用简化了编程，而且也为我们自定义水位线策略提供了模板。
这两个生成器可以通过调用WatermarkStrategy的静态辅助方法来创建。它们都是周期性生成水位线的，分别对应着处理有序流和乱序流的场景。 
（1）有序流
对于有序流，主要特点就是时间戳单调增长（Monotonously Increasing Timestamps），所以永远不会出现迟到数据的问题。这是周期性生成水位线的最简单的场景，直接调用WatermarkStrategy.forMonotonousTimestamps()方法就可以实现。简单来说，就是直接拿当前最大的时间戳作为水位线就可以了。
stream.assignTimestampsAndWatermarks(
        WatermarkStrategy.<Event>forMonotonousTimestamps()
                .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                    @Override
                    public long extractTimestamp(Event element, long recordTimestamp) {
                        return element.timestamp;
                    }
                })
);
上面代码中我们调用.withTimestampAssigner()方法，将数据中的timestamp字段提取出来，作为时间戳分配给数据元素；然后用内置的有序流水位线生成器构造出了生成策略。这样，提取出的数据时间戳，就是我们处理计算的事件时间。
这里需要注意的是，时间戳和水位线的单位，必须都是毫秒。
（2）乱序流
由于乱序流中需要等待迟到数据到齐，所以必须设置一个固定量的延迟时间（Fixed Amount of Lateness）。这时生成水位线的时间戳，就是当前数据流中最大的时间戳减去延迟的结果，相当于把表调慢，当前时钟会滞后于数据的最大时间戳。调用WatermarkStrategy. forBoundedOutOfOrderness()方法就可以实现。这个方法需要传入一个maxOutOfOrderness参数，表示“最大乱序程度”，它表示数据流中乱序数据时间戳的最大差值；如果我们能确定乱序程度，那么设置对应时间长度的延迟，就可以等到所有的乱序数据了。

```

##### watermark机制

```
Watermark是用于处理乱序事件的，用于衡量Event Time进展的机制。watermark可以翻译为水位线。

```

##### Watermark的核心原理

```
Watermark的核心本质可以理解成一个延迟触发机制。  

在 Flink 的窗口处理过程中，如果确定全部数据到达，就可以对 Window 的所有数据做 窗口计算操作（如汇总、分组等），如果数据没有全部到达，则继续等待该窗口中的数据全 部到达才开始处理。这种情况下就需要用到水位线（WaterMarks）机制，它能够衡量数据处 理进度（表达数据到达的完整性），保证事件数据（全部）到达 Flink 系统，或者在乱序及 延迟到达时，也能够像预期一样计算出正确并且连续的结果。当任何 Event 进入到 Flink 系统时，会根据当前最大事件时间产生 Watermarks 时间戳。
那么 Flink 是怎么计算 Watermak 的值呢？

Watermark =进入Flink 的最大的事件时间(mxtEventTime)-指定的延迟时间(t)
那么有 Watermark 的 Window 是怎么触发窗口函数的呢？  
如果有窗口的停止时间等于或者小于 maxEventTime - t(当时的warkmark)，那么这个窗口被触发执行。
其核心处理流程如下图所示。
```

![img](https://ask.qcloudimg.com/http-save/5642295/566ygbtca7.jpeg?imageView2/2/w/1620)

##### 二、Watermark的三种使用情况

##### 1、本来有序的Stream中的 Watermark

```
如果数据元素的事件时间是有序的，Watermark 时间戳会随着数据元素的事件时间按顺 序生成，此时水位线的变化和事件时间保持一直（因为既然是有序的时间，就不需要设置延迟了，那么t就是 0。所以 watermark=maxtime-0 = maxtime），也就是理想状态下的水位 线。当 Watermark 时间大于 Windows 结束时间就会触发对 Windows 的数据计算，以此类推， 下一个 Window 也是一样。**这种情况其实是乱序数据的一种特殊情况。**
```

##### 2、乱序事件中的Watermark

```
现实情况下数据元素往往并不是按照其产生顺序接入到 Flink 系统中进行处理，而频繁 出现乱序或迟到的情况，这种情况就需要使用 Watermarks 来应对。比如下图，设置延迟时间t为2。
```

##### 3、并行数据流中的Watermark

```
在多并行度的情况下，Watermark 会有一个对齐机制，这个对齐机制会取所有 Channel 中最小的 Watermark。
```

##### 窗口的概念

```
Flink是一种流式计算引擎，主要是来处理无界数据流的，数据源源不断、无穷无尽。想要更加方便高效地处理无界流，一种方式就是将无限数据切割成有限的“数据块”进行处理，这就是所谓的“窗口”（Window）。
在Flink中, 窗口就是用来处理无界流的核心。我们很容易把窗口想象成一个固定位置的“框”，数据源源不断地流过来，到某个时间点窗口该关闭了，就停止收集数据、触发计算并输出结果
这里注意为了明确数据划分到哪一个窗口，定义窗口都是包含起始时间、不包含结束时间的，用数学符号表示就是一个左闭右开的区间，例如0~10秒的窗口可以表示为[0, 10),这里单位为秒。
对于处理时间下的窗口而言，这样理解似乎没什么问题。因为窗口的关闭是基于系统时间的，赶不上这班车的数据，就只能坐下一班车了——正如上图中，0~10秒的窗口关闭后，可能还有时间戳为9的数据会来，它就只能进入10~20秒的窗口了。这样会造成窗口处理结果的不准确。
然而如果我们采用事件时间语义，就会有些费解了。由于有乱序数据，我们需要设置一个延迟时间来等所有数据到齐。比如上面的例子中，我们可以设置延迟时间为2秒，如图6-14所示，这样0~10秒的窗口会在时间戳为12的数据到来之后，才真正关闭计算输出结果，这样就可以正常包含迟到的9秒数据了。

我们可以梳理一下事件时间语义下，之前例子中窗口的处理过程：
（1）第一个数据时间戳为2，判断之后创建第一个窗口[0, 10），并将2秒数据保存进去；
（2）后续数据依次到来，时间戳均在 [0, 10）范围内，所以全部保存进第一个窗口；
（3）11秒数据到来，判断它不属于[0, 10）窗口，所以创建第二个窗口[10, 20），并将11秒的数据保存进去。由于水位线设置延迟时间为2秒，所以现在的时钟是9秒，第一个窗口也没有到关闭时间；
（4）之后又有9秒数据到来，同样进入[0, 10）窗口中；
（5）12秒数据到来，判断属于[10, 20）窗口，保存进去。这时产生的水位线推进到了10秒，所以 [0, 10）窗口应该关闭了。第一个窗口收集到了所有的7个数据，进行处理计算后输出结果，并将窗口关闭销毁；
（6）同样的，之后的数据依次进入第二个窗口，遇到20秒的数据时会创建第三个窗口[20, 30）并将数据保存进去；遇到22秒数据时，水位线达到了20秒，第二个窗口触发计算，输出结果并关闭。
这里需要注意的是，Flink中窗口并不是静态准备好的，而是动态创建——当有落在这个窗口区间范围的数据达到时，才创建对应的窗口。另外，这里我们认为到达窗口结束时间时，窗口就触发计算并关闭，事实上“触发计算”和“窗口关闭”两个行为也可以分开
```

##### 窗口的分类

```
我们最容易想到的就是按照时间段去截取数据，这种窗口就叫作“时间窗口”（Time Window）。这在实际应用中最常见，之前所举的例子也都是时间窗口。除了由时间驱动之外，窗口其实也可以由数据驱动，也就是说按照固定的个数，来截取一段数据集，这种窗口叫作“计数窗口”（Count Window）

时间窗口以时间点来定义窗口的开始（start）和结束（end），所以截取出的就是某一时间段的数据。到达结束时间时，窗口不再收集数据，触发计算输出结果，并将窗口关闭销毁。所以可以说基本思路就是“定点发车”。
用结束时间减去开始时间，得到这段时间的长度，就是窗口的大小（window size）。这里的时间可以是不同的语义，所以我们可以定义处理时间窗口和事件时间窗口。
Flink中有一个专门的类来表示时间窗口，名称就叫作TimeWindow。这个类只有两个私有属性：start和end，表示窗口的开始和结束的时间戳，单位为毫秒。

为什么不把窗口区间定义成左开右闭、包含上结束时间呢？这样maxTimestamp跟end一致，不就可以省去一个方法的定义吗？
这主要是为了方便判断窗口什么时候关闭。对于事件时间语义，窗口的关闭需要水位线推进到窗口的结束时间；而我们知道，水位线Watermark(t)代表的含义是“时间戳小于等于t的数据都已到齐，不会再来了”。为了简化分析，我们先不考虑乱序流设置的延迟时间。那么当新到一个时间戳为t的数据时，当前水位线的时间推进到了t – 1（还记得乱序流里生成水位线的减一操作吗？）。所以当时间戳为end的数据到来时，水位线推进到了end - 1；如果我们把窗口定义为不包含end，那么当前的水位线刚好就是maxTimestamp，表示窗口能够包含的数据都已经到齐，我们就可以直接关闭窗口了。所以有了这样的定义，我们就不需要再去考虑那烦人的“减一”了，直接看到时间戳为end的数据，就关闭对应的窗口。如果为乱序流设置了水位线延迟时间delay，也只需要等到时间戳为end + delay的数据，就可以关窗了。

计数窗口基于元素的个数来截取数据，到达固定的个数时就触发计算并关闭窗口。这相当于座位有限、“人满就发车”，是否发车与时间无关。每个窗口截取数据的个数，就是窗口的大小。
计数窗口相比时间窗口就更加简单，我们只需指定窗口大小，就可以把数据分配到对应的窗口中了。在Flink内部也并没有对应的类来表示计数窗口，底层是通过“全局窗口”（Global Window）来实现的。

时间窗口和计数窗口，只是对窗口的一个大致划分；在具体应用时，还需要定义更加精细的规则，来控制数据应该划分到哪个窗口中去。不同的分配数据的方式，就可以有不同的功能应用。
根据分配数据的规则，窗口的具体实现可以分为4类：滚动窗口（Tumbling Window）、滑动窗口（Sliding Window）、会话窗口（Session Window），以及全局窗口（Global Window）。下面我们来做具体介绍。

滚动窗口有固定的大小，是一种对数据进行“均匀切片”的划分方式。窗口之间没有重叠，也不会有间隔，是“首尾相接”的状态。如果我们把多个窗口的创建，看作一个窗口的运动，那就好像它在不停地向前“翻滚”一样。这是最简单的窗口形式，我们之前所举的例子都是滚动窗口。也正是因为滚动窗口是“无缝衔接”，所以每个数据都会被分配到一个窗口，而且只会属于一个窗口。
滚动窗口可以基于时间定义，也可以基于数据个数定义；需要的参数只有一个，就是窗口的大小（window size）。比如我们可以定义一个长度为1小时的滚动时间窗口，那么每个小时就会进行一次统计；或者定义一个长度为10的滚动计数窗口，就会每10个数进行一次统计。
滚动窗口应用非常广泛，它可以对每个时间段做聚合统计，很多BI分析指标都可以用它来实现。

与滚动窗口类似，滑动窗口的大小也是固定的。区别在于，窗口之间并不是首尾相接的，而是可以“错开”一定的位置。如果看作一个窗口的运动，那么就像是向前小步“滑动”一样。
既然是向前滑动，那么每一步滑多远，就也是可以控制的。所以定义滑动窗口的参数有两个：除去窗口大小（window size）之外，还有一个“滑动步长”（window slide），它其实就代表了窗口计算的频率。滑动的距离代表了下个窗口开始的时间间隔，而窗口大小是固定的，所以也就是两个窗口结束时间的间隔；窗口在结束时间触发计算输出结果，那么滑动步长就代表了计算频率。例如，我们定义一个长度为1小时、滑动步长为5分钟的滑动窗口，那么就会统计1小时内的数据，每5分钟统计一次。同样，滑动窗口可以基于时间定义，也可以基于数据个数定义。

会话窗口顾名思义，是基于“会话”（session）来来对数据进行分组的。这里的会话类似Web应用中session的概念，不过并不表示两端的通讯过程，而是借用会话超时失效的机制来描述窗口。简单来说，就是数据来了之后就开启一个会话窗口，如果接下来还有数据陆续到来，那么就一直保持会话；如果一段时间一直没收到数据，那就认为会话超时失效，窗口自动关闭。这就好像我们打电话一样，如果时不时总能说点什么，那说明还没聊完；如果陷入了尴尬的沉默，半天都没话说，那自然就可以挂电话了。
与滑动窗口和滚动窗口不同，会话窗口只能基于时间来定义，而没有“会话计数窗口”的概念。这很好理解，“会话”终止的标志就是“隔一段时间没有数据来”，如果不依赖时间而改成个数，就成了“隔几个数据没有数据来”，这完全是自相矛盾的说法。
而同样是基于这个判断标准，这“一段时间”到底是多少就很重要了，必须明确指定。对于会话窗口而言，最重要的参数就是这段时间的长度（size），它表示会话的超时时间，也就是两个会话窗口之间的最小距离。如果相邻两个数据到来的时间间隔（Gap）小于指定的大小（size），那说明还在保持会话，它们就属于同一个窗口；如果gap大于size，那么新来的数据就应该属于新的会话窗口，而前一个窗口就应该关闭了。在具体实现上，我们可以设置静态固定的大小（size），也可以通过一个自定义的提取器（gap extractor）动态提取最小间隔gap的值。
考虑到事件时间语义下的乱序流，这里又会有一些麻烦。相邻两个数据的时间间隔gap大于指定的size，我们认为它们属于两个会话窗口，前一个窗口就关闭；可在数据乱序的情况下，可能会有迟到数据，它的时间戳刚好是在之前的两个数据之间的。这样一来，之前我们判断的间隔中就不是“一直没有数据”，而缩小后的间隔有可能会比size还要小——这代表三个数据本来应该属于同一个会话窗口。
所以在Flink底层，对会话窗口的处理会比较特殊：每来一个新的数据，都会创建一个新的会话窗口；然后判断已有窗口之间的距离，如果小于给定的size，就对它们进行合并（merge）操作。在Window算子中，对会话窗口会有单独的处理逻辑。

还有一类比较通用的窗口，就是“全局窗口”。这种窗口全局有效，会把相同key的所有数据都分配到同一个窗口中；说直白一点，就跟没分窗口一样。无界流的数据永无止尽，所以这种窗口也没有结束的时候，默认是不会做触发计算的。如果希望它能对数据进行计算处理，还需要自定义“触发器”（Trigger）
```

##### 窗口API概览

```
按键分区（Keyed）和非按键分区（Non-Keyed）
在定义窗口操作之前，首先需要确定，到底是基于按键分区（Keyed）的数据流KeyedStream来开窗，还是直接在没有按键分区的DataStream上开窗。也就是说，在调用窗口算子之前，是否有keyBy操作。
（1）按键分区窗口（Keyed Windows）
经过按键分区keyBy操作后，数据流会按照key被分为多条逻辑流（logical streams），这就是KeyedStream。基于KeyedStream进行窗口操作时, 窗口计算会在多个并行子任务上同时执行。相同key的数据会被发送到同一个并行子任务，而窗口操作会基于每个key进行单独的处理。所以可以认为，每个key上都定义了一组窗口，各自独立地进行统计计算。
在代码实现上，我们需要先对DataStream调用.keyBy()进行按键分区，然后再调用.window()定义窗口。
stream.keyBy(...)
       .window(...)

（2）非按键分区（Non-Keyed Windows）
如果没有进行keyBy，那么原始的DataStream就不会分成多条逻辑流。这时窗口逻辑只能在一个任务（task）上执行，就相当于并行度变成了1。所以在实际应用中一般不推荐使用这种方式。
在代码中，直接基于DataStream调用.windowAll()定义窗口。
stream.windowAll(...)
这里需要注意的是，对于非按键分区的窗口操作，手动调大窗口算子的并行度也是无效的，windowAll本身就是一个非并行的操作

代码中窗口API的调用
窗口操作主要有两个部分：窗口分配器（Window Assigners）和窗口函数（Window Functions）。
stream.keyBy(<key selector>)
       .window(<window assigner>)
       .aggregate(<window function>)
其中.window()方法需要传入一个窗口分配器，它指明了窗口的类型；而后面的.aggregate()方法传入一个窗口函数作为参数，它用来定义窗口具体的处理逻辑。窗口分配器有各种形式，而窗口函数的调用方法也不只.aggregate()一种，我们接下来就详细展开讲解。
另外，在实际应用中，一般都需要并行执行任务，非按键分区很少用到，所以我们之后都以按键分区窗口为例；如果想要实现非按键分区窗口，只要前面不做keyBy，后面调用.window()时直接换成.windowAll()就可以了。

```

##### 窗口分配器（Window Assigners）

```
窗口分配器最通用的定义方式，就是调用.window()方法。这个方法需要传入一个WindowAssigner作为参数，返回WindowedStream。如果是非按键分区窗口，那么直接调用.windowAll()方法，同样传入一个WindowAssigner，返回的是AllWindowedStream。
窗口按照驱动类型可以分成时间窗口和计数窗口，而按照具体的分配规则，又有滚动窗口、滑动窗口、会话窗口、全局窗口四种。除去需要自定义的全局窗口外，其他常用的类型Flink中都给出了内置的分配器实现，我们可以方便地调用实现各种需求。


在较早的版本中，可以直接调用.timeWindow()来定义时间窗口；这种方式非常简洁，但使用事件时间语义时需要另外声明，程序员往往因为忘记这点而导致运行结果错误。所以在1.12版本之后，这种方式已经被弃用了，标准的声明方式就是直接调用.window()，在里面传入对应时间语义下的窗口分配器。这样一来，我们不需要专门定义时间语义，默认就是事件时间；如果想用处理时间，那么在这里传入处理时间的窗口分配器就可以了。

```

##### 滚动处理时间窗口

```
窗口分配器由类TumblingProcessingTimeWindows提供，需要调用它的静态方法.of()。

stream.keyBy(...)

.window(TumblingProcessingTimeWindows.of(Time.seconds(5)))

.aggregate(...)

这里.of()方法需要传入一个Time类型的参数size，表示滚动窗口的大小，我们这里创建了一个长度为5秒的滚动窗口。

另外，.of()还有一个重载方法，可以传入两个Time类型的参数：size和offset。第一个参数当然还是窗口大小，第二个参数则表示窗口起始点的偏移量。这里需要多做一些解释：对于我们之前的定义，滚动窗口其实只有一个size是不能唯一确定的。比如我们定义1天的滚动窗口，从每天的0点开始计时是可以的，统计的就是一个自然日的所有数据；而如果从每天的凌晨2点开始计时其实也完全没问题，只不过统计的数据变成了每天2点到第二天2点。这个起始点的选取，其实对窗口本身的类型没有影响；而为了方便应用，默认的起始点时间戳是窗口大小的整倍数。也就是说，如果我们定义1天的窗口，默认就从0点开始；如果定义1小时的窗口，默认就从整点开始。而如果我们非要不从这个默认值开始，那就可以通过设置偏移量offset来调整。

这里读者可能会觉得奇怪：这个功能好像没什么用，非要弄个偏移量不是给自己找别扭吗？这其实是有实际用途的。我们知道，不同国家分布在不同的时区。标准时间戳其实就是1970年1月1日0时0分0秒0毫秒开始计算的一个毫秒数，而这个时间是以UTC时间，也就是0时区（伦敦时间）为标准的。我们所在的时区是东八区，也就是UTC+8，跟UTC有8小时的时差。我们定义1天滚动窗口时，如果用默认的起始点，那么得到就是伦敦时间每天0点开启窗口，这时是北京时间早上8点。那怎样得到北京时间每天0点开启的滚动窗口呢？只要设置-8小时的偏移量就可以了：

.window(TumblingProcessingTimeWindows.of(Time.days(1), Time.hours(-8)))
```

##### 滑动处理时间窗口

```
窗口分配器由类SlidingProcessingTimeWindows提供，同样需要调用它的静态方法.of()。
stream.keyBy(...)
.window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
.aggregate(...)
这里.of()方法需要传入两个Time类型的参数：size和slide，前者表示滑动窗口的大小，后者表示滑动窗口的滑动步长。我们这里创建了一个长度为10秒、滑动步长为5秒的滑动窗口。
滑动窗口同样可以追加第三个参数，用于指定窗口起始点的偏移量，用法与滚动窗口完全一致。
```

##### 处理时间会话窗口

```
处理时间会话窗口
窗口分配器由类ProcessingTimeSessionWindows提供，需要调用它的静态方法.withGap()或者.withDynamicGap()。
stream.keyBy(...)
.window(ProcessingTimeSessionWindows.withGap(Time.seconds(10)))
.aggregate(...)
这里.withGap()方法需要传入一个Time类型的参数size，表示会话的超时时间，也就是最小间隔session gap。我们这里创建了静态会话超时时间为10秒的会话窗口。
.window(ProcessingTimeSessionWindows.withDynamicGap(new SessionWindowTimeGapExtractor<Tuple2<String, Long>>() {
    @Override
    public long extract(Tuple2<String, Long> element) { 
// 提取session gap值返回, 单位毫秒
        return element.f0.length() * 1000;
    }
}))
这里.withDynamicGap()方法需要传入一个SessionWindowTimeGapExtractor作为参数，用来定义session gap的动态提取逻辑。在这里，我们提取了数据元素的第一个字段，用它的长度乘以1000作为会话超时的间隔。
```

##### （4）滚动事件时间窗口

```
滚动事件时间窗口

窗口分配器由类TumblingEventTimeWindows提供，用法与滚动处理事件窗口完全一致。

stream.keyBy(...)

.window(TumblingEventTimeWindows.of(Time.seconds(5)))

.aggregate(...)

这里.of()方法也可以传入第二个参数offset，用于设置窗口起始点的偏移量。
```

##### （5）滑动事件时间窗口

```
窗口分配器由类SlidingEventTimeWindows提供，用法与滑动处理事件窗口完全一致。

stream.keyBy(...)

.window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))

.aggregate(...)
```

##### （6）事件时间会话窗口

```
窗口分配器由类EventTimeSessionWindows提供，用法与处理事件会话窗口完全一致。

stream.keyBy(...)

.window(EventTimeSessionWindows.withGap(Time.seconds(10)))

.aggregate(...)
```

##### 计数窗口

```
计数窗口概念非常简单，本身底层是基于全局窗口（Global Window）实现的。Flink为我们提供了非常方便的接口：直接调用.countWindow()方法。根据分配规则的不同，又可以分为滚动计数窗口和滑动计数窗口两类，下面我们就来看它们的具体实现。

（1）滚动计数窗口

滚动计数窗口只需要传入一个长整型的参数size，表示窗口的大小。

stream.keyBy(...)

.countWindow(10)

我们定义了一个长度为10的滚动计数窗口，当窗口中元素数量达到10的时候，就会触发计算执行并关闭窗口。

（2）滑动计数窗口

与滚动计数窗口类似，不过需要在.countWindow()调用时传入两个参数：size和slide，前者表示窗口大小，后者表示滑动步长。

stream.keyBy(...)

.countWindow(10，3)

我们定义了一个长度为10、滑动步长为3的滑动计数窗口。每个窗口统计10个数据，每隔3个数据就统计输出一次结果。
```

#####  全局窗口

```
全局窗口是计数窗口的底层实现，一般在需要自定义窗口时使用。它的定义同样是直接调用.window()，分配器由GlobalWindows类提供。

stream.keyBy(...)

.window(GlobalWindows.create());

需要注意使用全局窗口，必须自行定义触发器才能实现窗口计算，否则起不到任何作用。
```

##### 6.3.5 窗口函数（Window Functions）

```
定义了窗口分配器，我们只是知道了数据属于哪个窗口，可以将数据收集起来了；至于收集起来到底要做什么，其实还完全没有头绪。所以在窗口分配器之后，必须再接上一个定义窗口如何进行计算的操作，这就是所谓的“窗口函数”（window functions）。
经窗口分配器处理之后，数据可以分配到对应的窗口中，而数据流经过转换得到的数据类型是WindowedStream。这个类型并不是DataStream，所以并不能直接进行其他转换，而必须进一步调用窗口函数，对收集到的数据进行处理计算之后，才能最终再次得到DataStream
```

###### 增量聚合函数（incremental aggregation functions）

```
窗口将数据收集起来，最基本的处理操作当然就是进行聚合。窗口对无限流的切分，可以看作得到了一个有界数据集。如果我们等到所有数据都收集齐，在窗口到了结束时间要输出结果的一瞬间再去进行聚合，显然就不够高效了——这相当于真的在用批处理的思路来做实时流处理。

为了提高实时性，我们可以再次将流处理的思路发扬光大：就像DataStream的简单聚合一样，每来一条数据就立即进行计算，中间只要保持一个简单的聚合状态就可以了；区别只是在于不立即输出结果，而是要等到窗口结束时间。等到窗口到了结束时间需要输出计算结果的时候，我们只需要拿出之前聚合的状态直接输出，这无疑就大大提高了程序运行的效率和实时性。

典型的增量聚合函数有两个：ReduceFunction和AggregateFunction

（1）归约函数（ReduceFunction）
最基本的聚合方式就是归约（reduce）。我们在基本转换的聚合算子中介绍过reduce的用法，窗口的归约聚合也非常类似，就是将窗口中收集到的数据两两进行归约。当我们进行流处理时，就是要保存一个状态；每来一个新的数据，就和之前的聚合状态做归约，这样就实现了增量式的聚合。
窗口函数中也提供了ReduceFunction：只要基于WindowedStream调用.reduce()方法，然后传入ReduceFunction作为参数，就可以指定以归约两个元素的方式去对窗口中数据进行聚合了。这里的ReduceFunction其实与简单聚合时用到的ReduceFunction是同一个函数类接口，所以使用方式也是完全一样的。
我们回忆一下，ReduceFunction中需要重写一个reduce方法，它的两个参数代表输入的两个元素，而归约最终输出结果的数据类型，与输入的数据类型必须保持一致。也就是说，中间聚合的状态和输出的结果，都和输入的数据类型是一样的。

聚合函数
Flink的Window API中的aggregate就提供了这样的操作。直接基于WindowedStream调用.aggregate()方法，就可以定义更加灵活的窗口聚合操作。这个方法需要传入一个AggregateFunction的实现类作为参数。AggregateFunction在源码中的定义如下：
public interface AggregateFunction<IN, ACC, OUT> extends Function, Serializable {
    ACC createAccumulator();
    ACC add(IN value, ACC accumulator);
    OUT getResult(ACC accumulator);
    ACC merge(ACC a, ACC b);
}
AggregateFunction可以看作是ReduceFunction的通用版本，这里有三种类型：输入类型（IN）、累加器类型（ACC）和输出类型（OUT）。输入类型IN就是输入流中元素的数据类型；累加器类型ACC则是我们进行聚合的中间状态类型；而输出类型当然就是最终计算结果的类型了。
接口中有四个方法：
	createAccumulator()：创建一个累加器，这就是为聚合创建了一个初始状态，每个聚合任务只会调用一次。
	add()：将输入的元素添加到累加器中。这就是基于聚合状态，对新来的数据进行进一步聚合的过程。方法传入两个参数：当前新到的数据value，和当前的累加器accumulator；返回一个新的累加器值，也就是对聚合状态进行更新。每条数据到来之后都会调用这个方法。
	getResult()：从累加器中提取聚合的输出结果。也就是说，我们可以定义多个状态，然后再基于这些聚合的状态计算出一个结果进行输出。比如之前我们提到的计算平均值，就可以把sum和count作为状态放入累加器，而在调用这个方法时相除得到最终结果。这个方法只在窗口要输出结果时调用。
	merge()：合并两个累加器，并将合并后的状态作为一个累加器返回。这个方法只在需要合并窗口的场景下才会被调用；最常见的合并窗口（Merging Window）的场景就是会话窗口（Session Windows）。
所以可以看到，AggregateFunction的工作原理是：首先调用createAccumulator()为任务初始化一个状态(累加器)；而后每来一个数据就调用一次add()方法，对数据进行聚合，得到的结果保存在状态中；等到了窗口需要输出时，再调用getResult()方法得到计算结果。很明显，与ReduceFunction相同，AggregateFunction也是增量式的聚合；而由于输入、中间状态、输出的类型可以不同，使得应用更加灵活方便。
```

##### 全窗口函数（full window functions）

```
窗口操作中的另一大类就是全窗口函数。与增量聚合函数不同，全窗口函数需要先收集窗口中的数据，并在内部缓存起来，等到窗口要输出结果的时候再取出数据进行计算。
很明显，这就是典型的批处理思路了——先攒数据，等一批都到齐了再正式启动处理流程。这样做毫无疑问是低效的：因为窗口全部的计算任务都积压在了要输出结果的那一瞬间，而在之前收集数据的漫长过程中却无所事事。这就好比平时不用功，到考试之前通宵抱佛脚，肯定不如把工夫花在日常积累上。
那为什么还需要有全窗口函数呢？这是因为有些场景下，我们要做的计算必须基于全部的数据才有效，这时做增量聚合就没什么意义了；另外，输出的结果有可能要包含上下文中的一些信息（比如窗口的起始时间），这是增量聚合函数做不到的。所以，我们还需要有更丰富的窗口计算方式，这就可以用全窗口函数来实现。
在Flink中，全窗口函数也有两种：WindowFunction和ProcessWindowFunction。
（1）窗口函数（WindowFunction）
WindowFunction字面上就是“窗口函数”，它其实是老版本的通用窗口函数接口。我们可以基于WindowedStream调用.apply()方法，传入一个WindowFunction的实现类。
stream
    .keyBy(<key selector>)
    .window(<window assigner>)
    .apply(new MyWindowFunction());
这个类中可以获取到包含窗口所有数据的可迭代集合（Iterable），还可以拿到窗口（Window）本身的信息。WindowFunction接口在源码中实现如下：
public interface WindowFunction<IN, OUT, KEY, W extends Window> extends Function, Serializable {
void apply(KEY key, W window, Iterable<IN> input, Collector<OUT> out) throws Exception;
}
当窗口到达结束时间需要触发计算时，就会调用这里的apply方法。我们可以从input集合中取出窗口收集的数据，结合key和window信息，通过收集器（Collector）输出结果。这里Collector的用法，与FlatMapFunction中相同。
不过我们也看到了，WindowFunction能提供的上下文信息较少，也没有更高级的功能。事实上，它的作用可以被ProcessWindowFunction全覆盖，所以之后可能会逐渐弃用。一般在实际应用，直接使用ProcessWindowFunction就可以了。
（2）处理窗口函数（ProcessWindowFunction）
ProcessWindowFunction是Window API中最底层的通用窗口函数接口。之所以说它“最底层”，是因为除了可以拿到窗口中的所有数据之外，ProcessWindowFunction还可以获取到一个“上下文对象”（Context）。这个上下文对象非常强大，不仅能够获取窗口信息，还可以访问当前的时间和状态信息。这里的时间就包括了处理时间（processing time）和事件时间水位线（event time watermark）。这就使得ProcessWindowFunction更加灵活、功能更加丰富。事实上，ProcessWindowFunction是Flink底层API——处理函数（process function）中的一员。

3. 增量聚合和全窗口函数的结合使用
我们已经了解了Window API中两类窗口函数的用法，下面我们先来做个简单的总结。
增量聚合函数处理计算会更高效。举一个最简单的例子，对一组数据求和。大量的数据连续不断到来，全窗口函数只是把它们收集缓存起来，并没有处理；到了窗口要关闭、输出结果的时候，再遍历所有数据依次叠加，得到最终结果。而如果我们采用增量聚合的方式，那么只需要保存一个当前和的状态，每个数据到来时就会做一次加法，更新状态；到了要输出结果的时候，只要将当前状态直接拿出来就可以了。增量聚合相当于把计算量“均摊”到了窗口收集数据的过程中，自然就会比全窗口聚合更加高效、输出更加实时。
而全窗口函数的优势在于提供了更多的信息，可以认为是更加“通用”的窗口操作。它只负责收集数据、提供上下文相关信息，把所有的原材料都准备好，至于拿来做什么我们完全可以任意发挥。这就使得窗口计算更加灵活，功能更加强大。
所以在实际应用中，我们往往希望兼具这两者的优点，把它们结合在一起使用。Flink的Window API就给我们实现了这样的用法。
我们之前在调用WindowedStream的.reduce()和.aggregate()方法时，只是简单地直接传入了一个ReduceFunction或AggregateFunction进行增量聚合。除此之外，其实还可以传入第二个参数：一个全窗口函数，可以是WindowFunction或者ProcessWindowFunction。
// ReduceFunction与WindowFunction结合
public <R> SingleOutputStreamOperator<R> reduce(
        ReduceFunction<T> reduceFunction, WindowFunction<T, R, K, W> function) 
// ReduceFunction与ProcessWindowFunction结合
public <R> SingleOutputStreamOperator<R> reduce(
        ReduceFunction<T> reduceFunction, ProcessWindowFunction<T, R, K, W> function)
// AggregateFunction与WindowFunction结合
public <ACC, V, R> SingleOutputStreamOperator<R> aggregate(
        AggregateFunction<T, ACC, V> aggFunction, WindowFunction<V, R, K, W> windowFunction)
// AggregateFunction与ProcessWindowFunction结合
public <ACC, V, R> SingleOutputStreamOperator<R> aggregate(
        AggregateFunction<T, ACC, V> aggFunction,
        ProcessWindowFunction<V, R, K, W> windowFunction)
这样调用的处理机制是：基于第一个参数（增量聚合函数）来处理窗口数据，每来一个数据就做一次聚合；等到窗口需要触发计算时，则调用第二个参数（全窗口函数）的处理逻辑输出结果。需要注意的是，这里的全窗口函数就不再缓存所有数据了，而是直接将增量聚合函数的结果拿来当作了Iterable类型的输入。一般情况下，这时的可迭代集合中就只有一个元素了。
下面我们举一个具体的实例来说明。在网站的各种统计指标中，一个很重要的统计指标就是热门的链接；想要得到热门的url，前提是得到每个链接的“热门度”。一般情况下，可以用url的浏览量（点击量）表示热门度。我们这里统计10秒钟的url浏览量，每5秒钟更新一次；另外为了更加清晰地展示，还应该把窗口的起始结束时间一起输出。我们可以定义滑动窗口，并结合增量聚合函数和全窗口函数来得到统计结果。

 测试水位线和窗口的使用
之前讲过，当水位线到达窗口结束时间时，窗口就会闭合不再接收迟到的数据，因为根据水位线的定义，所有小于等于水位线的数据都已经到达，所以显然Flink会认为窗口中的数据都到达了（尽管可能存在迟到数据，也就是时间戳小于当前水位线的数据）。我们可以在之前生成水位线代码WatermarkTest的基础上，增加窗口应用做一下测试：
```

##### 触发器

```
1. 触发器（Trigger）
触发器主要是用来控制窗口什么时候触发计算。所谓的“触发计算”，本质上就是执行窗口函数，所以可以认为是计算得到结果并输出的过程。
基于WindowedStream调用.trigger()方法，就可以传入一个自定义的窗口触发器（Trigger）。
stream.keyBy(...)
       .window(...)
       .trigger(new MyTrigger())
Trigger是窗口算子的内部属性，每个窗口分配器（WindowAssigner）都会对应一个默认的触发器；对于Flink内置的窗口类型，它们的触发器都已经做了实现。例如，所有事件时间窗口，默认的触发器都是EventTimeTrigger；类似还有ProcessingTimeTrigger和CountTrigger。所以一般情况下是不需要自定义触发器的，不过我们依然有必要了解它的原理。
Trigger是一个抽象类，自定义时必须实现下面四个抽象方法：
	onElement()：窗口中每到来一个元素，都会调用这个方法。
	onEventTime()：当注册的事件时间定时器触发时，将调用这个方法。
	onProcessingTime ()：当注册的处理时间定时器触发时，将调用这个方法。
	clear()：当窗口关闭销毁时，调用这个方法。一般用来清除自定义的状态。
可以看到，除了clear()比较像生命周期方法，其他三个方法其实都是对某种事件的响应。onElement()是对流中数据元素到来的响应；而另两个则是对时间的响应。这几个方法的参数中都有一个“触发器上下文”（TriggerContext）对象，可以用来注册定时器回调（callback）。这里提到的“定时器”（Timer），其实就是我们设定的一个“闹钟”，代表未来某个时间点会执行的事件；当时间进展到设定的值时，就会执行定义好的操作。很明显，对于时间窗口（TimeWindow）而言，就应该是在窗口的结束时间设定了一个定时器，这样到时间就可以触发窗口的计算输出了。关于定时器的内容，我们在后面讲解处理函数（process function）时还会提到。
上面的前三个方法可以响应事件，那它们又是怎样跟窗口操作联系起来的呢？这就需要了解一下它们的返回值。这三个方法返回类型都是TriggerResult，这是一个枚举类型（enum），其中定义了对窗口进行操作的四种类型。
	CONTINUE（继续）：什么都不做
	FIRE（触发）：触发计算，输出结果
	PURGE（清除）：清空窗口中的所有数据，销毁窗口
	FIRE_AND_PURGE（触发并清除）：触发计算输出结果，并清除窗口
我们可以看到，Trigger除了可以控制触发计算，还可以定义窗口什么时候关闭（销毁）。上面的四种类型，其实也就是这两个操作交叉配对产生的结果。一般我们会认为，到了窗口的结束时间，那么就会触发计算输出结果，然后关闭窗口——似乎这两个操作应该是同时发生的；但TriggerResult的定义告诉我们，两者可以分开。稍后我们就会看到它们分开操作的场景。

2. 移除器（Evictor）
移除器主要用来定义移除某些数据的逻辑。基于WindowedStream调用.evictor()方法，就可以传入一个自定义的移除器（Evictor）。Evictor是一个接口，不同的窗口类型都有各自预实现的移除器。
stream.keyBy(...)
       .window(...)
       .evictor(new MyEvictor())
Evictor接口定义了两个方法：
	evictBefore()：定义执行窗口函数之前的移除数据操作
	evictAfter()：定义执行窗口函数之后的以处数据操作
默认情况下，预实现的移除器都是在执行窗口函数（window fucntions）之前移除数据的。

3. 允许延迟（Allowed Lateness）
在事件时间语义下，窗口中可能会出现数据迟到的情况。这是因为在乱序流中，水位线（watermark）并不一定能保证时间戳更早的所有数据不会再来。当水位线已经到达窗口结束时间时，窗口会触发计算并输出结果，这时一般也就要销毁窗口了；如果窗口关闭之后，又有本属于窗口内的数据姗姗来迟，默认情况下就会被丢弃。这也很好理解：窗口触发计算就像发车，如果要赶的车已经开走了，又不能坐其他的车（保证分配窗口的正确性），那就只好放弃坐班车了。
不过在多数情况下，直接丢弃数据也会导致统计结果不准确，我们还是希望该上车的人都能上来。为了解决迟到数据的问题，Flink提供了一个特殊的接口，可以为窗口算子设置一个“允许的最大延迟”（Allowed Lateness）。也就是说，我们可以设定允许延迟一段时间，在这段时间内，窗口不会销毁，继续到来的数据依然可以进入窗口中并触发计算。直到水位线推进到了 窗口结束时间 + 延迟时间，才真正将窗口的内容清空，正式关闭窗口。
基于WindowedStream调用.allowedLateness()方法，传入一个Time类型的延迟时间，就可以表示允许这段时间内的延迟数据。
stream.keyBy(...)
       .window(TumblingEventTimeWindows.of(Time.hours(1)))
       .allowedLateness(Time.minutes(1))
比如上面的代码中，我们定义了1小时的滚动窗口，并设置了允许1分钟的延迟数据。也就是说，在不考虑水位线延迟的情况下，对于8点~9点的窗口，本来应该是水位线到达9点整就触发计算并关闭窗口；现在允许延迟1分钟，那么9点整就只是触发一次计算并输出结果，并不会关窗。后续到达的数据，只要属于8点~9点窗口，依然可以在之前统计的基础上继续叠加，并且再次输出一个更新后的结果。直到水位线到达了9点零1分，这时就真正清空状态、关闭窗口，之后再来的迟到数据就会被丢弃了。
从这里我们就可以看到，窗口的触发计算（Fire）和清除（Purge）操作确实可以分开。不过在默认情况下，允许的延迟是0，这样一旦水位线到达了窗口结束时间就会触发计算并清除窗口，两个操作看起来就是同时发生了。当窗口被清除（关闭）之后，再来的数据就会被丢弃。
4. 将迟到的数据放入侧输出流
我们自然会想到，即使可以设置窗口的延迟时间，终归还是有限的，后续的数据还是会被丢弃。如果不想丢弃任何一个数据，又该怎么做呢？
Flink还提供了另外一种方式处理迟到数据。我们可以将未收入窗口的迟到数据，放入“侧输出流”（side output）进行另外的处理。所谓的侧输出流，相当于是数据流的一个“分支”，这个流中单独放置那些错过了该上的车、本该被丢弃的数据。
基于WindowedStream调用.sideOutputLateData() 方法，就可以实现这个功能。方法需要传入一个“输出标签”（OutputTag），用来标记分支的迟到数据流。因为保存的就是流中的原始数据，所以OutputTag的类型与流中数据类型相同。
DataStream<Event> stream = env.addSource(...);

OutputTag<Event> outputTag = new OutputTag<Event>("late") {};

stream.keyBy(...)
       .window(TumblingEventTimeWindows.of(Time.hours(1)))
.sideOutputLateData(outputTag)
将迟到数据放入侧输出流之后，还应该可以将它提取出来。基于窗口处理完成之后的DataStream，调用.getSideOutput()方法，传入对应的输出标签，就可以获取到迟到数据所在的流了。
SingleOutputStreamOperator<AggResult> winAggStream = stream.keyBy(...)
       .window(TumblingEventTimeWindows.of(Time.hours(1)))
.sideOutputLateData(outputTag)
.aggregate(new MyAggregateFunction())
DataStream<Event> lateStream = winAggStream.getSideOutput(outputTag);
这里注意，getSideOutput()是SingleOutputStreamOperator的方法，获取到的侧输出流数据类型应该和OutputTag指定的类型一致，与窗口聚合之后流中的数据类型可以不同。

```

##### 窗口的生命周期

```
1. 窗口的创建
窗口的类型和基本信息由窗口分配器（window assigners）指定，但窗口不会预先创建好，而是由数据驱动创建。当第一个应该属于这个窗口的数据元素到达时，就会创建对应的窗口。
2. 窗口计算的触发
除了窗口分配器，每个窗口还会有自己的窗口函数（window functions）和触发器（trigger）。窗口函数可以分为增量聚合函数和全窗口函数，主要定义了窗口中计算的逻辑；而触发器则是指定调用窗口函数的条件。
对于不同的窗口类型，触发计算的条件也会不同。例如，一个滚动事件时间窗口，应该在水位线到达窗口结束时间的时候触发计算，属于“定点发车”；而一个计数窗口，会在窗口中元素数量达到定义大小时触发计算，属于“人满就发车”。所以Flink预定义的窗口类型都有对应内置的触发器。
对于事件时间窗口而言，除去到达结束时间的“定点发车”，还有另一种情形。当我们设置了允许延迟，那么如果水位线超过了窗口结束时间、但还没有到达设定的最大延迟时间，这期间内到达的迟到数据也会触发窗口计算。这类似于没有准时赶上班车的人又追上了车，这时车要再次停靠、开门，将新的数据整合统计进来。
3. 窗口的销毁
一般情况下，当时间达到了结束点，就会直接触发计算输出结果、进而清除状态销毁窗口。这时窗口的销毁可以认为和触发计算是同一时刻。这里需要注意，Flink中只对时间窗口（TimeWindow）有销毁机制；由于计数窗口（CountWindow）是基于全局窗口（GlobalWindw）实现的，而全局窗口不会清除状态，所以就不会被销毁。
在特殊的场景下，窗口的销毁和触发计算会有所不同。事件时间语义下，如果设置了允许延迟，那么在水位线到达窗口结束时间时，仍然不会销毁窗口；窗口真正被完全删除的时间点，是窗口的结束时间加上用户指定的允许延迟时间。

Window API首先按照时候按键分区分成两类。keyBy之后的KeyedStream，可以调用.window()方法声明按键分区窗口（Keyed Windows）；而如果不做keyBy，DataStream也可以直接调用.windowAll()声明非按键分区窗口。之后的方法调用就完全一样了。
接下来首先是通过.window()/.windowAll()方法定义窗口分配器，得到WindowedStream；然后通过各种转换方法（reduce/aggregate/apply/process）给出窗口函数(ReduceFunction/AggregateFunction/ProcessWindowFunction)，定义窗口的具体计算处理逻辑，转换之后重新得到DataStream。这两者必不可少，是窗口算子（WindowOperator）最重要的组成部分。
此外，在这两者之间，还可以基于WindowedStream调用.trigger()自定义触发器、调用.evictor()定义移除器、调用.allowedLateness()指定允许延迟时间、调用.sideOutputLateData()将迟到数据写入侧输出流，这些都是可选的API，一般不需要实现。而如果定义了侧输出流，可以基于窗口聚合之后的DataStream调用.getSideOutput()获取侧输出流。
```

##### 迟到数据的处理

```
设置水位线延迟时间
水位线是事件时间的进展，它是我们整个应用的全局逻辑时钟。水位线生成之后，会随着数据在任务间流动，从而给每个任务指明当前的事件时间。所以从这个意义上讲，水位线是一个覆盖万物的存在，它并不只针对事件时间窗口有效。
之前我们讲到触发器时曾提到过“定时器”，时间窗口的操作底层就是靠定时器来控制触发的。既然是底层机制，定时器自然就不可能是窗口的专利了；事实上它是Flink底层API——处理函数（process function）的重要部分。
所以水位线其实是所有事件时间定时器触发的判断标准。那么水位线的延迟，当然也就是全局时钟的滞后，相当于是上帝拨动了琴弦，所有人的表都变慢了。
既然水位线这么重要，那一般情况就不应该把它的延迟设置得太大，否则流处理的实时性就会大大降低。因为水位线的延迟主要是用来对付分布式网络传输导致的数据乱序，而网络传输的乱序程度一般并不会很大，大多集中在几毫秒至几百毫秒。所以实际应用中，我们往往会给水位线设置一个“能够处理大多数乱序数据的小延迟”，视需求一般设在毫秒~秒级。
当我们设置了水位线延迟时间后，所有定时器就都会按照延迟后的水位线来触发。如果一个数据所包含的时间戳，小于当前的水位线，那么它就是所谓的“迟到数据”。
6.4.2 允许窗口处理迟到数据
水位线延迟设置的比较小，那之后如果仍有数据迟到该怎么办？对于窗口计算而言，如果水位线已经到了窗口结束时间，默认窗口就会关闭，那么之后再来的数据就要被丢弃了。
自然想到，Flink的窗口也是可以设置延迟时间，允许继续处理迟到数据的。
这种情况下，由于大部分乱序数据已经被水位线的延迟等到了，所以往往迟到的数据不会太多。这样，我们会在水位线到达窗口结束时间时，先快速地输出一个近似正确的计算结果；然后保持窗口继续等到延迟数据，每来一条数据，窗口就会再次计算，并将更新后的结果输出。这样就可以逐步修正计算结果，最终得到准确的统计值了。
类比班车的例子，我们可以这样理解：大多数人是在发车时刻前后到达的，所以我们只要把表调慢，稍微等一会儿，绝大部分人就都上车了，这个把表调慢的时间就是水位线的延迟；到点之后，班车就准时出发了，不过可能还有该来的人没赶上。于是我们就先慢慢往前开，这段时间内，如果迟到的人抓点紧还是可以追上的；如果有人追上来了，就停车开门让他上来，然后车继续向前开。当然我们的车不能一直慢慢开，需要有一个时间限制，这就是窗口的允许延迟时间。一旦超过了这个时间，班车就不再停留，开上高速疾驰而去了。
所以我们将水位线的延迟和窗口的允许延迟数据结合起来，最后的效果就是先快速实时地输出一个近似的结果，而后再不断调整，最终得到正确的计算结果。回想流处理的发展过程，这不就是著名的Lambda架构吗？原先需要两套独立的系统来同时保证实时性和结果的最终正确性，如今Flink一套系统就全部搞定了。
6.4.3 将迟到数据放入窗口侧输出流
即使我们有了前面的双重保证，可窗口不能一直等下去，最后总要真正关闭。窗口一旦关闭，后续的数据就都要被丢弃了。那如果真的还有漏网之鱼又该怎么办呢？
那就要用到最后一招了：用窗口的侧输出流来收集关窗以后的迟到数据。这种方式是最后“兜底”的方法，只能保证数据不丢失；因为窗口已经真正关闭，所以是无法基于之前窗口的结果直接做更新的。我们只能将之前的窗口计算结果保存下来，然后获取侧输出流中的迟到数据，判断数据所属的窗口，手动对结果进行合并更新。尽管有些烦琐，实时性也不够强，但能够保证最终结果一定是正确的。
如果还用赶班车来类比，那就是车已经上高速开走了，这班车是肯定赶不上了。不过我们还留下了行进路线和联系方式，迟到的人如果想办法辗转到了目的地，还是可以和大部队会合的。最终，所有该到的人都会在目的地出现。
所以总结起来，Flink处理迟到数据，对于结果的正确性有三重保障：水位线的延迟，窗口允许迟到数据，以及将迟到数据放入窗口侧输出流。我们可以回忆一下之前6.3.5小节统计每个url浏览次数的代码UrlViewCountExample，
```

