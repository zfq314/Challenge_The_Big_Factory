#### clickhouse卸载安装

```
yum list installed | grep clickhouse
yum remove -y clickhouse-*

rm -rf /var/lib/clickhouse
rm -rf /etc/clickhouse-*
rm -rf /var/log/clickhouse-server

rpm -ivh *.rpm 

vim /etc/clickhouse-server/config.xml  修改配置文件放开listen host

systemctl start clickhouse-server 启动clickhouse服务

systemctl disable clickhouse-server 关闭开机自启

clickhouse-client -m 客户端访问


clikhouse 密码修改
https://blog.csdn.net/chengyuqiang/article/details/108534587

切换目录
 cd /etc/clickhouse-server/
 
修改密码 
vim user.xml password 123456
SHA256密码加密：

echo -n 123456 | openssl dgst -sha256

获取  (stdin)= 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92 

修改默认密码 user.d/ default-password

客户端进入 ：clickhouse-client --host 127.0.0.1 --port 9933 --password 123456
```

#### 数据类型

```
整型
固定长度的整型，包括有符号整型或无符号整型。
整型范围（-2n-1~2n-1-1）：
Int8 - [-128 : 127]
Int16 - [-32768 : 32767]
Int32 - [-2147483648 : 2147483647]
Int64 - [-9223372036854775808 : 9223372036854775807]
无符号整型范围（0~2n-1）：
UInt8 - [0 : 255]
UInt16 - [0 : 65535]
UInt32 - [0 : 4294967295]
UInt64 - [0 : 18446744073709551615]
使用场景： 个数、数量、也可以存储型id。

浮点型
Float32 - float
Float64 – double
建议尽可能以整数形式存储数据。例如，将固定精度的数字转换为整数值，如时间用毫秒为单位表示，因为浮点型进行计算时可能引起四舍五入的误差
使用场景：一般数据值比较小，不涉及大量的统计计算，精度要求不高的时候。比如保存商品的重量


布尔型
没有单独的类型来存储布尔值。可以使用 UInt8 类型，取值限制为 0 或 1。


Decimal 型
有符号的浮点数，可在加、减和乘法运算过程中保持精度。对于除法，最低有效数字会被丢弃（不舍入）。
有三种声明：
	Decimal32(s)，相当于Decimal(9-s,s)，有效位数为1~9
	Decimal64(s)，相当于Decimal(18-s,s)，有效位数为1~18
	Decimal128(s)，相当于Decimal(38-s,s)，有效位数为1~38
s标识小数位
使用场景： 一般金额字段、汇率、利率等字段为了保证小数点精度，都使用Decimal进行存储。



字符串
1）String
字符串可以任意长度的。它可以包含任意的字节集，包含空字节。
2）FixedString(N)
固定长度 N 的字符串，N 必须是严格的正自然数。当服务端读取长度小于 N 的字符串时候，通过在字符串末尾添加空字节来达到 N 字节长度。 当服务端读取长度大于 N 的字符串时候，将返回错误消息。
与String相比，极少会使用FixedString，因为使用起来不是很方便。
使用场景：名称、文字描述、字符型编码。 固定长度的可以保存一些定长的内容，比如一些编码，性别等但是考虑到一定的变化风险，带来收益不够明显，所以定长字符串使用意义有限。

枚举类型
包括 Enum8 和 Enum16 类型。Enum 保存 'string'= integer 的对应关系。
Enum8 用 'String'= Int8 对描述。
Enum16 用 'String'= Int16 对描述。

枚举类型
包括 Enum8 和 Enum16 类型。Enum 保存 'string'= integer 的对应关系。
Enum8 用 'String'= Int8 对描述。
Enum16 用 'String'= Int16 对描述。
1）用法演示
创建一个带有一个枚举 Enum8('hello' = 1, 'world' = 2) 类型的列
CREATE TABLE t_enum
(
    x Enum8('hello' = 1, 'world' = 2)
)
ENGINE = TinyLog;

这个 x 列只能存储类型定义中列出的值：'hello'或'world'
INSERT INTO t_enum VALUES ('hello'), ('world'), ('hello');

如果尝试保存任何其他值，ClickHouse 抛出异常

 insert into t_enum values('a')
 
 Unknown element 'a' for enum, maybe you meant: ['a']: data for INSERT was parsed from query
 
 如果需要看到对应行的数值，则必须将 Enum 值转换为整数类型
 SELECT CAST(x, 'Int8') FROM t_enum;
 
 使用场景：对一些状态、类型的字段算是一种空间优化，也算是一种数据约束。但是实际使用中往往因为一些数据内容的变化增加一定的维护成本，甚至是数据丢失问题。所以谨慎使用。
 
 时间类型
目前ClickHouse 有三种时间类型
	Date接受年-月-日的字符串比如 ‘2019-12-16’
	Datetime接受年-月-日 时:分:秒的字符串比如 ‘2019-12-16 20:50:10’
	Datetime64接受年-月-日 时:分:秒.亚秒的字符串比如‘2019-12-16 20:50:10.66’
日期类型，用两个字节存储，表示从 1970-01-01 (无符号) 到当前的日期值。
还有很多数据结构，可以参考官方文档：https://clickhouse.yandex/docs/zh/data_types/


数组
Array(T)：由 T 类型元素组成的数组。
T 可以是任意类型，包含数组类型。 但不推荐使用多维数组，ClickHouse 对多维数组的支持有限。例如，不能在MergeTree表中存储多维数组。

（1）创建数组方式1，使用array函数
array(T) SELECT array(1, 2) AS x, toTypeName(x) ;

[] SELECT [1, 2] AS x, toTypeName(x);
```

#### 表引擎

```
表引擎的使用
表引擎是ClickHouse的一大特色。可以说， 表引擎决定了如何存储表的数据。包括：
	数据的存储方式和位置，写到哪里以及从哪里读取数据。
	支持哪些查询以及如何支持。
	并发数据访问。
	索引的使用（如果存在）。
	是否可以执行多线程请求。
	数据复制参数。
表引擎的使用方式就是必须显式在创建表时定义该表使用的引擎，以及引擎使用的相关参数。
特别注意：引擎的名称大小写敏感

 TinyLog
以列文件的形式保存在磁盘上，不支持索引，没有并发控制。一般保存少量数据的小表，生产环境上作用有限。可以用于平时练习测试用。
如：
create table t_tinylog ( id String, name String) engine=TinyLog;

Memory
内存引擎，数据以未压缩的原始形式直接保存在内存当中，服务器重启数据就会消失。读写操作不会相互阻塞，不支持索引。简单查询下有非常非常高的性能表现（超过10G/s）。
一般用到它的地方不多，除了用来测试，就是在需要非常高的性能，同时数据量又不太大（上限大概 1 亿行）的场景。

MergeTree
ClickHouse中最强大的表引擎当属MergeTree（合并树）引擎及该系列（*MergeTree）中的其他引擎，支持索引和分区，地位可以相当于innodb之于Mysql。 而且基于MergeTree，还衍生除了很多小弟，也是非常有特色的引擎。


1）建表语句
create table t_order_mt(id UInt32,sku_id String,total_amount Decimal(16,2),create_time Datetime) engine =MergeTree partition by toYYYYMMDD(create_time) primary key (id) order by (id,sku_id);
2）插入数据
insert into  t_order_mt values (101,'sku_001',1000.00,'2020-06-01 12:00:00') ,(102,'sku_002',2000.00,'2020-06-01 11:00:00'),(102,'sku_004',2500.00,'2020-06-01 12:00:00'),(102,'sku_002',2000.00,'2020-06-01 13:00:00'),(102,'sku_002',12000.00,'2020-06-01 13:00:00'),(102,'sku_002',600.00,'2020-06-02 12:00:00');
MergeTree其实还有很多参数(绝大多数用默认值即可)，但是三个参数是更加重要的，也涉及了关于MergeTree的很多概念。

partition by 分区(可选)
1）作用
学过hive的应该都不陌生，分区的目的主要是降低扫描的范围，优化查询速度
2）如果不填
只会使用一个分区。
3）分区目录
MergeTree 是以列文件+索引文件+表定义文件组成的，但是如果设定了分区那么这些文件就会保存到不同的分区目录中。
4）并行
分区后，面对涉及跨分区的查询统计，ClickHouse会以分区为单位并行处理。
5）数据写入与分区合并
任何一个批次的数据写入都会产生一个临时分区，不会纳入任何一个已有的分区。写入后的某个时刻（大概10-15分钟后），ClickHouse会自动执行合并操作（等不及也可以手动通过optimize执行），把临时分区的数据，合并到已有分区中。
optimize table xxxx final;


insert into  t_order_mt values (101,'sku_001',1000.00,'2020-06-01 12:00:00') ,(102,'sku_002',2000.00,'2020-06-01 11:00:00'),(102,'sku_004',2500.00,'2020-06-01 12:00:00'),(102,'sku_002',2000.00,'2020-06-01 13:00:00'),(102,'sku_002',12000.00,'2020-06-01 13:00:00'),(102,'sku_002',600.00,'2020-06-02 12:00:00');

手动optimize之后
hadoop102 :) optimize table t_order_mt final;

```

┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 102 │ sku_002 │       600.00 │ 2020-06-02 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 101 │ sku_001 │      1000.00 │ 2020-06-01 12:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 11:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_002 │     12000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_004 │      2500.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 102 │ sku_002 │       600.00 │ 2020-06-02 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 101 │ sku_001 │      1000.00 │ 2020-06-01 12:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 11:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_002 │     12000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_004 │      2500.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘

```
primary key主键(可选)
ClickHouse中的主键，和其他数据库不太一样，它只提供了数据的一级索引，但是却不是唯一约束。这就意味着是可以存在相同primary key的数据的。
主键的设定主要依据是查询语句中的where 条件。
根据条件通过对主键进行某种形式的二分查找，能够定位到对应的index granularity,避免了全表扫描。
index granularity： 直接翻译的话就是索引粒度，指在稀疏索引中两个相邻索引对应数据的间隔。ClickHouse中的MergeTree默认是8192。官方不建议修改这个值，除非该列存在大量重复值，比如在一个分区中几万行才有一个不同数据。

order by（必选）
order by 设定了分区内的数据按照哪些字段顺序进行有序保存。
order by是MergeTree中唯一一个必填项，甚至比primary key 还重要，因为当用户不设置主键的情况，很多处理会依照order by的字段进行处理（比如后面会讲的去重和汇总）。
要求：主键必须是order by字段的前缀字段。
比如order by 字段是 (id,sku_id)  那么主键必须是id 或者(id,sku_id)
```

#### 二级索引

```
目前在ClickHouse的官网上二级索引的功能在v20.1.2.4之前是被标注为实验性的，在这个版本之后默认是开启的。
set allow_experimental_data_skipping_indices=1;
error:DB::Exception: Unknown setting allow_experimental_data_skipping_indices. 

创建表
创建测试表
create table t_order_mt2(
    id UInt32,
    sku_id String,
    total_amount Decimal(16,2),
    create_time  Datetime,
	INDEX a total_amount TYPE minmax GRANULARITY 5
 ) engine =MergeTree
   partition by toYYYYMMDD(create_time)
   primary key (id)
   order by (id, sku_id);

sublime 编辑-行操作-合并行
create table t_order_mt2(id UInt32, sku_id String, total_amount Decimal(16,2), create_time  Datetime, INDEX a total_amount TYPE minmax GRANULARITY 5 ) engine =MergeTree partition by toYYYYMMDD(create_time) primary key (id) order by (id, sku_id);

其中GRANULARITY N 是设定二级索引对于一级索引粒度的粒度。

那么在使用下面语句进行测试，可以看出二级索引能够为非主键字段的查询发挥作用。
[root@hadoop31 ~]# clickhouse-client --host hadoop31 --port 9933 --password --send_logs_level=trace <<< 'select * from clickhouse.t_order_mt2  where total_amount > toDecimal32(900., 2)';

clickhouse.t_order_mt2 (6c25bbc0-ed95-4b0f-ac25-bbc0ed95fb0f) (SelectExecutor): Index `a` has dropped 1/2 granules.(执行结果)
```

##### 数据TTL

```
TTL即Time To Live，MergeTree提供了可以管理数据表或者列的生命周期的功能。

1）列级别TTL
（1）创建测试表
create table t_order_mt3(id UInt32, sku_id String, total_amount Decimal(16,2)  TTL create_time+interval 10 SECOND, create_time  Datetime ) engine =MergeTree partition by toYYYYMMDD(create_time) primary key (id) order by (id, sku_id);

插入数据
insert into  t_order_mt3 values
(106,'sku_001',1000.00,'2020-06-12 22:52:30'),
(107,'sku_002',2000.00,'2020-06-12 22:52:30'),
(110,'sku_003',600.00,'2020-06-13 12:00:00');
手动合并optimize table t_order_mt3 final;
alter table t_order_mt3 MODIFY TTL create_time + INTERVAL 10 SECOND;

涉及判断的字段必须是Date或者Datetime类型，推荐使用分区的日期字段。
能够使用的时间周期：
- SECOND
- MINUTE
- HOUR
- DAY
- WEEK
- MONTH
- QUARTER
- YEAR
```

#### ReplacingMergeTree表引擎

```
ReplacingMergeTree是MergeTree的一个变种，它存储特性完全继承MergeTree，只是多了一个去重的功能。 尽管MergeTree可以设置主键，但是primary key其实没有唯一约束的功能。如果你想处理掉重复的数据，可以借助这个ReplacingMergeTree。

1）去重时机
数据的去重只会在合并的过程中出现。合并会在未知的时间在后台进行，所以你无法预先作出计划。有一些数据可能仍未被处理。
2）去重范围
如果表经过了分区，去重只会在分区内部进行去重，不能执行跨分区的去重。
所以ReplacingMergeTree能力有限， ReplacingMergeTree 适用于在后台清除重复的数据以节省空间，但是它不保证没有重复的数据出现。


案例演示
（1）创建表
create table t_order_rmt(
    id UInt32,
    sku_id String,
    total_amount Decimal(16,2) ,
    create_time  Datetime 
 ) engine =ReplacingMergeTree(create_time)
   partition by toYYYYMMDD(create_time)
   primary key (id)
   order by (id, sku_id);
ReplacingMergeTree() 填入的参数为版本字段，重复数据保留版本字段值最大的。
如果不填版本字段，默认按照插入顺序保留最后一条。   

（2）向表中插入数据
insert into  t_order_rmt values
(101,'sku_001',1000.00,'2020-06-01 12:00:00') ,
(102,'sku_002',2000.00,'2020-06-01 11:00:00'),
(102,'sku_004',2500.00,'2020-06-01 12:00:00'),
(102,'sku_002',2000.00,'2020-06-01 13:00:00'),
(102,'sku_002',12000.00,'2020-06-01 13:00:00'),
(102,'sku_002',600.00,'2020-06-02 12:00:00');
合并数据
OPTIMIZE TABLE t_order_rmt FINAL;
通过测试得到结论
	实际上是使用order by 字段作为唯一键
	去重不能跨分区
	只有同一批插入（新版本）或合并分区时才会进行去重
	认定重复的数据保留，版本字段值最大的
	如果版本字段相同则按插入顺序保留最后一笔

```

####  SummingMergeTree

```
对于不查询明细，只关心以维度进行汇总聚合结果的场景。如果只使用普通的MergeTree的话，无论是存储空间的开销，还是查询时临时聚合的开销都比较大。

ClickHouse 为了这种场景，提供了一种能够“预聚合”的引擎SummingMergeTree

1）案例演示
（1）创建表
create table t_order_smt(
    id UInt32,
    sku_id String,
    total_amount Decimal(16,2) ,
    create_time Datetime 
 ) engine =SummingMergeTree(total_amount)
   partition by toYYYYMMDD(create_time)
   primary key (id)
   order by (id,sku_id );
 （2）插入数据
insert into  t_order_smt values
(101,'sku_001',1000.00,'2020-06-01 12:00:00'),
(102,'sku_002',2000.00,'2020-06-01 11:00:00'),
(102,'sku_004',2500.00,'2020-06-01 12:00:00'),
(102,'sku_002',2000.00,'2020-06-01 13:00:00'),
(102,'sku_002',12000.00,'2020-06-01 13:00:00'),
(102,'sku_002',600.00,'2020-06-02 12:00:00');
手动合并
OPTIMIZE TABLE t_order_smt FINAL;
select * from t_order_smt;

2）通过结果可以得到以下结论
	以SummingMergeTree（）中指定的列作为汇总数据列
	可以填写多列必须数字列，如果不填，以所有非维度列且为数字列的字段为汇总数据列
	以order by 的列为准，作为维度列
	其他的列按插入顺序保留第一行
	不在一个分区的数据不会被聚合
	只有在同一批次插入(新版本)或分片合并时才会进行聚合
3）开发建议
设计聚合表的话，唯一键值、流水号可以去掉，所有字段全部是维度、度量或者时间戳。
4）问题
能不能直接执行以下SQL得到汇总值
select total_amount from  XXX where province_name=’’ and create_date=’xxx’
不行，可能会包含一些还没来得及聚合的临时明细
如果要是获取汇总值，还是需要使用sum进行聚合，这样效率会有一定的提高，但本身ClickHouse是列式存储的，效率提升有限，不会特别明显。
select sum(total_amount) from province_name=’’ and create_date=‘xxx’

```

