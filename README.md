# Challenge_The_Big_Factory 项目模板文件

### 查看日历里面的错误信息

```
tail -200000f yarn-root-resourcemanager-hadoop31.log |grep -i error
```

### 查看当前目录下所有文件/文件夹的大小 

```
du -sh ./* 
```

### yarn报不健康信息 

```
/default-rack	UNHEALTHY	
解决方法 

* 1 把节点上的不用的东西删完，删到90%以下即可 
* 2 在yarn-site.xml中添加以下配置信息，修改上限和下限

<property>
     <name>yarn.nodemanager.disk-health-checker.min-healthy-disks</name>
     <value>0.0</value>
  </property>
  <property>
     <name>yarn.nodemanager.disk-health-checker.max-disk-utilization-per-disk-percentage</name>
     <value>100.0</value>
 </property>
```

### rpm卸载

```
rpm 卸载 jdk 
rpm -qa |grep openjdk|xargs -n1 rpm -e --nodeps 
```

### yum源修改

```
mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup
切换目录
cd /etc/yum.repos.d/

获取yum源 wget -O,  --output-document=FILE    将文档写入 FILE。
wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
yum makecache 
yum -y update
```

### 环境变量配置

```
#JAVA_HOME
export JAVA_HOME=/opt/module/jdk1.8.0_212
export PATH=$PATH:$JAVA_HOME/bin
```

### apache源码编译

```
mvn clean package -DskipTests
```

### Linux 统计某个目录下文件的个数

```
ls -l | grep "^-" | wc -l
```

### hadoop数据均衡命令

```
start-balancer.sh -threshold 2 对于参数2，代表的是集群中各个节点的磁盘利用率相差不超过2%，可根据实际情况进行调正
需要手动停止
stop-balancer.sh
```

### findyi  关于目标

```
彼时也经历过长期的痛苦和自省，最后给未来三年设定了两个长期目标，同时这也是公众号名称findyi的来源：不断探索、寻找人生真正值得追求的目标。
我找到目标的方法是在放空的状态下不断和自己对话，不断提出问题、否定问题、解决问题、确定问题。
如果大家也迷失于没有目标，可以尝试先放空自己，然后和自己不断对话。
关于目标还想唠叨几句，经常有读者会怼我：你别天天分享成长之类的东西，有些人喜欢躺平，不喜欢有目标的生活。
很遗憾哪怕你要躺平除非家里真有矿，否则你一样要制定一个躺平的目标，并拆解实现躺平的要素，聚焦去实现了。否则所谓的躺平就不是躺平，而是给未来的自己埋大雷。
简单点说就是：当咸鱼也需要资格！
```

### linux获取日期函数

```
date +"%Y%m01" #当月第一天
date -d"$(date -d"1 month" +"%Y%m01") -1 day" +"%Y%m%d" #当月最后一天
date +"%Y%m$(cal|sed 'N;${s/.* //;P;d};D')" #当月最后一天
```

### linux  脚本  -n 命令

```
# 检测字符串长度是否不为 0，不为 0 返回 true。 -n 判断变量的值
# - 变量的值，为空，返回1，为false
# - 变量的值，非空，返回0，为true

if [ -n "$1" ] ;then
    $hive -e "$sql1"
else 
    $hive -e "$sql2"
fi
```

### xsync同步脚本

```sh
#!/bin/bash
#1. 判断参数个数
if [ $# -lt 1 ]
then
  echo Not Enough Arguement!
  exit;
fi
#2. 遍历集群所有机器
for host in hadoop102 hadoop103 hadoop104
do
  echo ====================  $host  ====================
  #3. 遍历所有目录，挨个发送
  for file in $@
  do
    #4 判断文件是否存在
    if [ -e $file ]
    then
      #5. 获取父目录
      pdir=$(cd -P $(dirname $file); pwd)
      #6. 获取当前文件的名称
      fname=$(basename $file)
      ssh $host "mkdir -p $pdir"
      rsync -av $pdir/$fname $host:$pdir
    else
      echo $file does not exists!
    fi
  done
done
```

### rpm 文件卸载

```
sudo rpm -qa |grep -i java|xargs -n1 sudo rpm -e --nodeps 

#!/bin/bash
cd /data/program/project/applog; java -jar gmall2020-mock-log-2021-10-10.jar >/dev/null 2>&1 & 
```

### xcall.sh 脚本 $*



```
#! /bin/bash
for i in hadoop102 hadoop103 hadoop104
do
    echo --------- $i ----------
    ssh $i "$*"
done
```

### 项目经验之Hadoop参数调优

  

```
HDFS参数调优hdfs-site.xml

NameNode有一个工作线程池，用来处理不同DataNode的并发心跳以及客户端并发的元数据操作。
对于大集群或者有大量客户端的集群来说，通常需要增大参数dfs.namenode.handler.count的默认值10。
<property>
    <name>dfs.namenode.handler.count</name>
    <value>10</value>
</property>

计算公式： dfs.namenode.handler.count=20×〖log〗_e^(Cluster Size)
例如： 用python 计算
import math
print int(20*math.log(8))

python 退出 函数 quit()
```

### YARN参数调优yarn-site.xml

```
面临问题：数据统计主要用HiveSQL，没有数据倾斜，小文件已经做了合并处理，开启的JVM重用，而且IO没有阻塞，内存用了不到50%。但是还是跑的非常慢，而且数据量洪峰过来时，整个集群都会宕掉。基于这种情况有没有优化方案。
解决办法：
内存利用率不够。这个一般是Yarn的2个配置造成的，单个任务可以申请的最大内存大小，和Hadoop单个节点可用内存大小。调节这两个参数能提高系统内存的利用率。
（a）yarn.nodemanager.resource.memory-mb
表示该节点上YARN可使用的物理内存总量，默认是8192（MB），注意，如果你的节点内存资源不够8GB，则需要调减小这个值，而YARN不会智能的探测节点的物理内存总量。
（b）yarn.scheduler.maximum-allocation-mb
单个任务可申请的最多物理内存量，默认是8192（MB）。
```

```
<property>
            <name>yarn.nodemanager.vmem-pmem-ratio</name>
            <value>8</value>
            <description>任务每使用1MB物理内存，最多可使用虚拟内存量比率，默认2.1；在上一项中设置为false不检测虚拟内存时，此项就无意义了</description> 
    </property>  
    <property>
            <name>yarn.nodemanager.resource.cpu-vcores</name>
            <value>12</value>
            <description>该节点上YARN可使用的总核心数；一般设为cat /proc/cpuinfo| grep "processor"| wc -l 的值。默认是8个；</description>  
    </property>
    <property>
            <name>yarn.nodemanager.resource.memory-mb</name>
            <value>16384</value>
            <description>该节点上YARN可使用的物理内存总量，【向操作系统申请的总量】默认是8192（MB）</description>  
    </property>
    <property>
            <name>yarn.scheduler.minimum-allocation-mb</name>
            <value>4096</value>
            <description>单个容器/调度器可申请的最少物理内存量，默认是1024（MB）；一般每个contain都分配这个值；即：capacity memory:3072, vCores:1，如果提示物理内存溢出，提高这个值即可；</description> 
    </property>
    <property>
            <name>yarn.scheduler.maximum-allocation-mb</name>
            <value>8000</value>
            <description>单个容器申请最大值</description> 
    </property>  
```

### 人民日报语句摘录

``` 
日子是自己的，你不努力，没人能替你成长。坚持读书、定期健身、凡事提前五分钟……别错过每一个自我成长的机会，别辜负时光，去做人生的主角。

坚持读书
　　读书能让我们知道世界之辽阔，哪怕未曾行万里路，也可尽“享”万千风景。人生任何一个阶段，都不应该放弃读书这件事。每天坚持读书半小时，不但会拓宽你的眼界，更会提升你的格局。
定期健身
　　生活再忙碌，都要留出时间健身。要知道，身体动起来，思维也会活跃起来。战胜惰性，定期健身，你流下的每一滴汗水，都是在帮助你成就更好的人生。
发现美好瞬间
　　郊游那天天气晴朗，偶然间吃到了好吃的甜品，工作上有了点小成就，学习中解出了一道难题......只要你善于发现，美好遍地都是。去成为一个像小太阳一样的人吧，发现无数个美好瞬间，充满朝气地去影响身边人。
时常自我反省
　　一个人只有清楚地认识自己，不断改进，才能成长为更好的人。如果能够时常自我反省，能够在错误中寻找解决策略并加以修正，就会不断得到提升和进步。
凡事提前5分钟
　　“凡事预则立，不预则废。” 上学提前5分钟，和客户见面提前5分钟，和朋友约会提前5分钟......哪怕只有这5分钟，也能更有效地帮助你把控时间，树立靠谱的外在形象。
好好说话
　　俗语说：“良言一句三冬暖，恶语伤人六月寒。”我们都会说话，却不一定都能做到好好说话。有时候心里是为对方好，话到嘴边却变了味，关系也在这样的沟通中越来越差。好好说话，于己于人，都是最大的善良。
常怀感恩之心
　　没有谁是一座孤岛，多想想他人的好。感恩爱人的患难与共，感恩朋友的鼓励和扶持，感恩身边一点一滴的温暖。常怀感恩之心，才能拥有感知幸福的能力，才会有源源不断的爱的回流。
不随意评价别人
　　有一种教养，叫做不随意评价。一个真正懂得尊重别人的人，不会随便说出那些令对方尴尬的话。哪怕对方做得真的不尽人意，也会讲究说话方式，真诚地提出建议和改进方法。
远离计较和纠缠
　　生活中，我们免不了会碰见烦心事。可有些事越是计较，越是容易纠缠不休。最聪明的做法是及时止损，远离它们。远离不必要的计较和纠缠，才会活得更舒心、自在。
保持自立
　　一个人如果没有主见，凡事依赖别人，就容易处处被动。无论何时何地，都要保持自立，做一个有主见的人。自己的事情自己拿主意，树立目标，做好计划，靠自己的能力去解决问题。
```

### 当前目录下的大小各个目录的大小（常用 ）

```
du -h --max-depth=1
```

### flinkcdc 需要的权限

```sql
GRANT SELECT, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'user' IDENTIFIED BY 'password';
```

### doris学习

```shell
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo docker run -it -v /opt/module/apache-maven-3.8.5/resporsities:/root/.m2  -v /opt/module/incubator-doris-0.15.0-rc04/:/root/incubator-doris-0.15.0-rc04/ apache/incubator-doris:build-env-for-0.15.0
/opt/module/jdk1.8.0_144
```

### 实时和离线的处理工具

```
sparksql处理离线数据，替代hive
flinksql处理实时数据，开发实时DW

两个都要，同时还要规划集群资源：CPU、内存、网络、存储
```

### maven项目依赖无法下载的问题

```
使用cmd，到项目根目录下，运行命令：
mvn clean install -Dmaven.test.skip=true

使用cmd，到项目根目录下，运行命令：
mvn -U idea:idea
```

### #zk.sh

```sh
#!/bin/bash
case $1 in
"start"){
    for i in hdp1 hdp2 hdp3
    do
        echo ---------- zookeeper $i 启动 ------------
        ssh $i "/opt/module/zookeeper-3.5.7/bin/zkServer.sh start"
    done
};;
"stop"){
    for i in hdp1 hdp2 hdp3
    do
        echo ---------- zookeeper $i 停止 ------------    
        ssh $i "/opt/module/zookeeper-3.5.7/bin/zkServer.sh stop"
    done
};;
"status"){
    for i in hdp1 hdp2 hdp3
    do
        echo ---------- zookeeper $i 状态 ------------    
        ssh $i "/opt/module/zookeeper-3.5.7/bin/zkServer.sh status"
    done
};;
esac
```

### #kf.sh

```sh
#!/bin/bash

case $1 in
"start"){
    for i in hdp1 hdp2 hdp3
    do
        echo " --------启动 $i Kafka-------"
        ssh $i "/opt/module//opt/module/kafka_2.11-2.4.1/bin/kafka-server-start.sh -daemon /opt/module//opt/module/kafka_2.11-2.4.1/config/server.properties "
    done
};;
"stop"){
    for i in hdp1 hdp2 hdp3
    do
        echo " --------停止 $i Kafka-------"
        ssh $i "/opt/module//opt/module/kafka_2.11-2.4.1/bin/kafka-server-stop.sh stop"
    done
};;
esac
```

### mac 切换root用户  sudo -s

```
/bin/zsh -c "$(curl -fsSL https://gitee.com/cunkai/HomebrewCN/raw/master/Homebrew.sh)"
```

### flinkcdc 官网

```
https://github.com/ververica/flink-cdc-connectors.git
```

### hadoop集群

```
hadoop 集群密码：
hadoop-sh2020@dc.com
```

### Mac mysql 

```
密码：zfq120514
```

### mysql获取表注释

```sql
SELECT
table_name 表名,
table_comment 表说明
FROM
information_schema.TABLES
WHERE
table_schema = 'decent_cloud' and table_comment like '%财务平账单%'
ORDER BY
table_name
```

