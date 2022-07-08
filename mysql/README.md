

##### mysql索引

```sql
msyql 8.x:
			1. 默认字符集由latin1变为utf8mb4
			2. MyISAM系统表全部换成InnoDB表
			3. 自增变量持久化
			4. DDL原子化
			5. 参数修改持久化
			6. 新增降序索引
			7. group by 不再隐式排序
			8. JSON特性增强
			9. redo & undo 日志加密
			10. innodb select for update跳过锁等待
			11. 增加SET_VAR语法
			12. 支持不可见索引
			13. 支持直方图
			14. 新增innodb_dedicated_server参数
			15. 日志分类更详细
			16. undo空间自动回收
			17. 增加资源组
			18. 增加角色管理
			19. 支持窗口函数
			开窗的应用
			       
                   select 
                field 
        from 
           (SELECT CONCAT(column_name,',') AS field,ORDINAL_POSITION as a1 ,lead(ORDINAL_POSITION,1) over(ORDER BY ORDINAL_POSITION asc) as a2  FROM information_schema.COLUMNS WHERE  table_schema='decent_cloud' and table_name = "t_fast_package" ORDER BY ORDINAL_POSITION asc
                 ) m 
                 where m.a2 is not null 
        union all
        select 
                field 
        from 
           (SELECT column_name AS field,ORDINAL_POSITION as a1 ,lead(ORDINAL_POSITION,1) over(ORDER BY ORDINAL_POSITION asc) as a2  FROM information_schema.COLUMNS WHERE  table_schema='decent_cloud' and table_name = "t_fast_package" ORDER BY ORDINAL_POSITION asc
                        ) m 
                        where m.a2 is  null
                        
```

##### sqlserver版本获取

```
 select @@version
```

##### mysql索引

```
MySQL官方对索引的定义为：索引（Index）是帮助MySQL高效获取数据的数据结构，可以得到索引的本质，索引是一种数据结构。

你可以简单的理解为索引是排好序的快速查找数据结构

一般来说，索引本身也很大，不可能全部存储在内存中。因此索引往往以索引文件的形式存储在磁盘上，我们平成所说的索引，如果没有特别指明，都是B树（多路搜索树，并不一定是二叉树）结构组织的索引。
```

##### 索引的数据结构

```
BTREE

B树（Balance Tree多路平衡查找树）

Hash索引

full-text全文索引

R-Tree索引
```

##### 索引的分类

```
单值索引

即一个索引只包含单个列，一个表可以有多个单列索引

唯一索引

索引的值必须唯一，但允许有空值

UNIQUE KEY

复合索引

即一个索引包含多个列
```

##### 索引操作基本语法

```
-- 创建索引
create UNIQUE index name_index on user (name)
-- 查看索引
show index  from user;
--  删除索引
drop index name_index on  user 

​ 使用EXPLAIN关键字可以模拟优化器执行SQL查询语句，从而知道MySQL是如何处理你的SQL语句的。分析你的查询语句或是表结构的想能瓶颈。

id

select查询的序列号，包含一组数字。表示查询中执行select自重或操作表的顺序。
其中共有三种情况：
id相同，执行顺序由上至下。
id不同，如果是子查询，id的序号会递增。id的值越大，优先级越高。越先执行。
id相同不同，同时存在

select_type

分类：
simple :
简单的select查询，查询中不包含子查询或者UNION
primary :
按照主键查询
SUNQUERY:
子查询
DEIVED:
衍生查询，在from列表中包含的子查询被标记为DERIVED（衍生）MySQL会递归执行这些子查询，把结果放在临时表里。
UNION:
若第二个SELECT出现在UNION之后，则被标记为UNION，若UNION包含在FROM子句的子查询中，外层SELECT将被标记为：DERIVED
UNION RESULT：
从UNION表获取结果的SELECT

table

显示这一行的数据是关于哪张表的

type

显示查询使用了何种索引类型

从最好到最差依次为：

system > const > eq_ref > ref > range >index > ALL

system：
表只有一行记录,这是const类型的特列

const:

按索引字段的值进行条件查询

eq_ref:

唯一性索引扫描，对于每个索引键，表中只有一条记录与之匹配。常见于主键或唯一索引扫描。

ref:
非唯一性索引扫描，返回匹配某个单独值得所有行。

本质上也是一种索引访问，它返回所有匹配某个单独值的行，然而，它可能会找到多个符合条件的行，所以他应该属于查找和扫描的混合体。

range:
范围查询，比如id字段上存在索引。

select * from user where id < 10
1
以上sql就是range类型的查询。

也就是说当我们查询的时候，它会把所有的id拿出来筛选小于10的数据。

index:
select id from user
1
id字段存在索引，遍历索引树。所以就是index类型。效率比全表扫描略高。

ALL:
全表扫描，没有用到索引。

一般我们再查询时，建议类型一般在range级别以上。

possiable_keys
显示可能应用在这张表中的索引，一个或者多个。
查询涉及到的字段上所存在索引，则该索引将被列出。单不一定被查询实际使用

key

实际使用的的索引，如果为null,则没有使用索引
查询中若使用了覆盖索引，则该索引和查询的select字段重叠

key_len
查询索引字段的长度

key_len

显示索引的那一列被使用了，如果是常量则为const

rows
每次查询的行数
```

##### 索引失效问题

```
在查询时，我们应尽量避免索引失效问题

在查询时，尽量使用全值匹配，特别是在使用复合索引时。
最佳左前缀法则： 如果索引了多列，要遵守最左前缀法则，指的是查询从索引的最左前列开始，不跳过索引中间的列
不在索引上做任何操作（计算，函数，自动或手动类型转换），会导致索引失效转向全表扫描。
存储引擎不能使用索引中范围条件最右边的列，范围条件右侧索引会全部失效。
尽量使用覆盖索引（只访问索引的查询（索隐列和查询列一直)),尽量避免 select * 操作。
mysql 在使用 != 或<>时有时候无法使用索引会导致全表扫描。
注意null/not null对索引可能的影响
like 以通配符开头，mysql索引会失效变成全表扫描。
字符串不加单引号索引会失效
尽量避免使用or关键字，使用or也会导致索引失效。
```

##### 索引demo 

```sql
-- 创建索引
create UNIQUE index name_index on user (name)
create UNIQUE index name_un on user (name,id,age,email)
-- 查看索引
show index  from user;
--  删除索引
drop index name_un on  user 

explain select email id,name,age from user  where id <>5;

-- 创建表和一些假数据：
create table if not exists `article`(
`id` int(10) unsigned not null primary key auto_increment,
`author_id` int(10) unsigned not null,
`category_id` int(10) unsigned not null,
`views` int(10) unsigned not null,
`comments` int(10) unsigned not null,
`title` varbinary(255) not null,
`content` text not null);

insert into `article`(`author_id`,`category_id`,`views`,`comments`,`title`,`content`) values
(1,1,1,1,'1','1'),
(2,2,2,2,'2','2'),
(1,1,3,3,'3','3');
-- 查询category_id =1并且comments大于1，被看过最多那条记录的id，看看没有加索引的情况下的执行计划：
explain select id,author_id from article where category_id = 1 and comments > 1 order by views desc limit 1;
-- type=ALL表示全表扫描，并且Extra有Using filesort，存在filesort表示查询效果很坏。
-- 因我们查询使用category_id,comments,views三个字段，尝试给它们3个字段添加索引：
create index idx_article_ccv on article(`category_id`,`comments`,`views`);
-- type=range,使用到了索引，但是Extra 还是有filesort，这是不行的。但是我们已经建立索引了，为什么会没有用到。这是因为BTree索引工作原理，先排序category_id.如果遇到相同的category_id则再排序comments,如果遇到comments再排序views,当comments字段联合索引处于中间位置时，因为comments>1条件为范围值，这样MySQL无法利用索引再对后面的views部分进行检索，即range类型查询字段后面的索引无效。

drop index idx_article_ccv on article;
-- 更换索引给category_id,views创建索引
create index idx_article_cv on article(category_id,views);

-- 可以看到type=ref，索引类型更好了。并且Extra没有filesort，category_id为覆盖索引第一个它功能用于查找，第二个索引views在order by 后面用于排序。不会像上例中第二个索引用于排序后造成第三个索引失效，从而导致sql内部使用filesort进行排序。

create table if not exists `class`(
`id` int(10) unsigned not null auto_increment,
`card` int(10) unsigned not null,
primary key(`id`)
);

create table if not exists `book`(
`bookid` int(10) unsigned not null auto_increment,
`card` int(10) unsigned not null,
primary key(`bookid`)
);
# 添加一些数据：
insert into class(card) values(floor(1+(rand()*20)));

insert into book(card) values(floor(1+(rand()*20)));

select * from class ;

select * from book ;

-- 在没有建立索引状态下查询执行计划：

explain select * from class left join book on book.card = class.card;

-- 显然这样很糟糕，2个查询type都为ALL，这样导致全表扫描。
-- 当然创建索引并不是一下子就创建成功的，我们需要不断的调换寻求最佳方法，所以首先给右表book的card添加索引：


alter table `book` add index Y (`card`);

show index from book
-- 左连接(left join)将索引加在右表中，type=ref非唯一性索引扫描，并且rows也由原来20+21变成20+1
# 删除之前创建索引
drop index Y on book;
# 给左表class的card添加索引
alter table `class` add index Y (`card`);

-- 可以type=index,当然不如上面创建索引ref好，并且rows为20+21，也不如上面创建的索引好。
-- 这么看来左连接把索引加在右表上会比较好

-- 左连接，索引创建右表，右连接索引建在左表上。
# 当然你也可以通过调换2个表位置，也是可以的


create table if not exists `phone`(
`phoneid` int(10) unsigned not null auto_increment,
`card` int(10) unsigned not null,
primary key(`phoneid`)
)engine=innodb;

# 添加点假数据
insert into phone(card) values(floor(1+(rand()*20)));

select * from phone;

explain select * from class left join book on class.card = book.card left join phone on book.card = phone.card;


-- 显然type=ALL全表扫描，key都为NULL没有用到索引。

-- 因查询方向是class-->book--->phone,连接方式为left join,那么从双表分析得到一些诀窍，给book和phone添加索引

alter table `phone` add index z (`card`);
alter table `book` add index Y (`card`);

-- 执行计划后两行的type都成为ref,有人会问为什么第一行type还是ALL，当然第一个执行语句需要全表扫描来驱动整个sql语句。而且看rows时候20+1+1 当然好过 20+21+20.读取记录的行数也少了不少。

-- - 尽可能减少join语句的NestedLoop循环总次数，永远用小的结果集驱动大的结果集.
-- - 优先优化NestedLoop的内层循环 （鸡蛋黄，鸡蛋清，鸡蛋壳道理）
-- - 保证Join语句中被驱动表上Join条件字段已经被索引。
-- - 当无法保证被驱动表的Join条件字段被索引且内存资源充足的前提下，不要太吝啬JoinBuffer的设置。
```

##### 索引样例

```sql
create table if not exists `article`(
`id` int(10) unsigned not null primary key auto_increment,
`author_id` int(10) unsigned not null,
`category_id` int(10) unsigned not null,
`views` int(10) unsigned not null,
`comments` int(10) unsigned not null,
`title` varbinary(255) not null,
`content` text not null
);
insert into `article` ( `author_id`,`category_id`,`views`, `comments`,`title`,`content`) 
values
(1,1,1,1,'1','1'),
(2,2,2,2,'2','2'),
(1,1,3,3,'3','3');
-- 查询category_id为1且comments大于1的情况下,views最多的article_id
show index from article;
explain select id,author_id from article  where category_id=1 and comments>1 order by views desc limit 1;
-- 结论：很显然，type是all，即最坏的情况。extra里还出现了using filesort，也是最坏的情况。优化是必须的。

-- 新建索引+删除索引
create index idx_article_ccv on article(category_id,comments,views);   # 复合索引
explain select id,author_id from article  where category_id=1 and comments>1 # 范围查找后，索引失效，文件排序
order by views desc limit 1; 

explain select id,author_id from article  where category_id=1 and comments=1 # 范围查找后，索引失效
order by views desc limit 1; 

-- 结论:
-- type变成了range,这是可以忍受的。但是extra里使用Using filesort仍是无法接受的。
-- 
-- 但是我们已经建立了索引,为啥没用呢?
-- 
-- 这是因为按照BTree索引的工作原理，
-- 
-- 先排序category_id,
-- 
-- 如果遇到相同的category_id,则再排序comments,如果遇到相同的comments则再排序views.
-- 
-- 当comments字段在联合素引里处于中间位置时，
-- 
-- 因comments > 1条件是一个范围值(所谓range),
-- 
-- MySQL无法利用索引再对后面的views部分进行检索,即range类型查询字段后面的索引无效。
-- 
-- 3.删除第一次建立的索引
show index from article;
drop index idx_article_ccv on article;


-- alter table article add index idx_article_cv ('category_id' , 'views');
 
create index idx_article_cv on article(category_id, views);\

explain select id,author_id from article  where category_id=1 and comments>1 # 范围查找后，索引失效，文件排序
order by views desc limit 1; 
-- 结论：
-- 
-- 可以看到，type变为了ref，extra中的using filesort也消失了，结果非常理想。

create table if not exists class(
id int unsigned not null auto_increment,
card int unsigned not null,
primary key (id)
);
 
create table if not exists book(
bookid int unsigned not null auto_increment,
card int unsigned not null,
primary key (bookid)
);
 truncate table class;
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into class(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
insert into book(card) values (floor(1+(rand()*20)));
explain select * from  class left join book  on class.card=book.card

show index from book;
-- 创建索引
alter table class add index card_index (card);
alter table book add index card_indexss (card);
drop index card_index on book;

-- 可以看到第二行的type变为了ref，rows也减少了许多。


-- 分析：由rows可以看到，两张表都扫描了20行，相比第二步可见性能区别。
-- 这是由左连接特性决定的。left join条件用于确定如何从右表搜索行，左边一定都有，因此两个表扫描的行数都是20。
-- 所以，右边是我们的关键点，一定需要建立索引。

-- 优化较明显。这是因为RIGHTJOIN条件用于确定如何从左表搜索行,右边一定都有,所以左边是我们的关键点,一定需要建立索引。

create table if not exists phone(
phoneid int unsigned not null auto_increment,
card int unsigned not null,
primary key (phoneid)
);
 
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));
insert into phone(card) values (floor(1+(rand()*20)));


alter table `phone` add index z (`card`);
alter table `book` add index Y (`card`);

explain select * from class left join book on class.card = book.card left join phone on book.card = phone.card;

-- 结论：join语句的优化
-- 
-- 尽可能减少join语句中的NestedLoop(嵌套循环)的循环总次数：“永远用小结果集驱动大的结果集”小表驱动大表。
-- 
-- 优先优化NestedLoop的内存循环；
-- 
-- 保证join语句中被驱动表上join条件字段已经被索引；
-- 
-- 当无法保证被驱动表的join条件字段被索引且内存资源充足的前提下，不要太吝啬joinBuffer的设置。

-- 索引失效

drop table staffs;
create table staffs(
id int auto_increment,
name varchar(24) not null  comment '姓名',
age int not null  comment '年龄',
pos varchar(20) not null   comment '职位',
add_time timestamp not null     comment '入职时间',
primary key(id)
) charset utf8 comment '员工记录表';
insert into staffs(name,age,pos,add_time) values('z3',22,'manager',now());
insert into staffs(name,age,pos,add_time) values('July',23,'dev',now());
insert into staffs(name,age,pos,add_time) values('2000',23,'dev' ,now());
select * from staffs;
create index idx_staffs_nameAgePos on staffs(name,age,pos);
show index from staffs;



-- 全值匹配我最爱
select * from staffs;
explain select * from staffs where name='July';
explain select * from staffs where name='July' and age=23;
explain select * from staffs where name='July' and age=23 and pos='dev';
-- 最佳左前缀法则

-- 如果索引了多个列，要遵循左前缀法则，指查询需要从索引的最左前列开始，并且按照索引列中的顺序去查询，若跳过则索引失效。

explain select * from staffs where age=23 and pos='dev'; -- type=ALL 带头大哥死了

explain select * from staffs where name='July'; -- -- type=ref 带头大哥没有死


-- 索引列上做某些操作，会导致索引失效
-- 不在索引列上做任何操作(计算、函数、类型转换(手动or自动))，会导致索引失效，转向全表扫描
explain select * from staffs where LEFT(name,4)='July' -- 索引列有表达式导致索引失效 

-- 索引列上使用了表达式，如where substr(a,1,3)= "hhh’，where a =a +1，表达式是一大忌讳，再简单mysq也不认。

-- 有时数据量不是大到严重影响速度时，一般可以先查出来，比如先查所有有订单记录的数据，再在程序中去筛选
-- 存储引擎不能使用索引中范围条件右边的列

explain select * from staffs where name='July' and age=25 and pos='manager'; -- type=ref 
explain select * from staffs where name='July' and age>25 and pos='manager'; -- type=range 范围之后索引失效，pos索引没有生效

-- 尽量使用覆盖索引(只访问索引列(查询列和索引列一致))，减少select *

explain select * from staffs where NAME='July' and age=25 and pos='manager'; 
explain select name,age,pos from staffs where NAME='July' and age=25 and pos='manager'; -- Extra Using index 


-- mysql在使用不等于运算符(!=或<>)时，无法使用索引，会导致全表扫描。

explain select * from staffs where name='July'; -- type= ref ref=const
explain select * from staffs where name !='July'; -- type= range
explain select * from staffs where name <> 'July'; -- type= range
-- is null和is not null，无法使用索引

explain select * from staffs where name is not null  
explain select * from staffs where name is null 

-- like若以通配符开头(‘%aa’)，则索引失效，全表扫描

explain select * from staffs where name like '%July'; -- type all 

explain select * from staffs where name like '%July%'; -- type all 
explain select * from staffs where name like 'July%';  -- type range  百分like写右面 

-- 如何解决like%字符串%时，索引不被使用的问题？ 解决：通过覆盖索引的方式，解决该问题。

create index age_name_index on staffs(name,age);

explain select id,name,age,pos,add_time from staffs  where name like 'l%'

字符串不加引号导致索引失效
少用or，用它来连接条件时，索引失效
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/324150246f34427089fe4118c37c5950.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBA5rKJ5rO9wrc=,size_20,color_FFFFFF,t_70,g_se,x_16)

![在这里插入图片描述](https://img-blog.csdnimg.cn/5641ed2e187148889d83319fc2635a0f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBA5rKJ5rO9wrc=,size_20,color_FFFFFF,t_70,g_se,x_16)



![在这里插入图片描述](https://img-blog.csdnimg.cn/33f6abe158aa415f80f5015b20264b05.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBA5rKJ5rO9wrc=,size_20,color_FFFFFF,t_70,g_se,x_16)

##### 索引小总结

```
小总结
1.带头大哥不能死

2.中间兄弟不能断

3.索引列上无计算

4.like百分加右边

5.范围之后全失效

6.字符串里有引号

全值匹配我最爱，最左前缀要遵守；
带头大哥不能死，中间兄弟不能断；
索引列上少计算，范围之后全失效；
Like百分写最右，覆盖索引不写星；
不等空值还有or，索引失效要少用；
VAR引号不可丢，SQL高级也不难！
 注意：使用like ’%kk%‘，需要考虑覆盖索引，若不满足覆盖索引，则不会使用到索引。
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/3b894cbfce1a4bd0b29662d0b51a6930.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBA5rKJ5rO9wrc=,size_20,color_FFFFFF,t_70,g_se,x_16)

##### mysql一列多值改成多行,借助msyql的系统函数

```sql
select t.*,
substring_index(substring_index(technology_purity_identity,',',b.help_topic_id+1),',',-1) as purity_jq from (
select 
			a.*, -- 1,2,3 technology_purity_identity 值
			b.showroom_name 
	from t_fast_customer a left join t_showroom b on a.showroom_identity=b.showroom_identity
	where a.customer_code='KH003975' and b.showroom_identity='ba4ead54-decc-11ea-9cfd-aecc6b4ae066'
)t
join
  mysql.help_topic b
  on b.help_topic_id < (length(t.technology_purity_identity) - length(replace(t.technology_purity_identity,',',''))+1) 
```

##### 	mysql exists

```sql
select exists (select * from t_showroom where showroom_name='fq展厅'); -- exists(true)-1/0    exists(true)-1->1 exists(true)-0->0
select exists (select * from t_showroom where showroom_name='深圳展厅'); -- exists(true)-1/0    exists(true)-1->1 exists(true)-0->0		
```

```
-- 中文排序的时候需要转换下编码格式解决问题
order by xh, convert(plm using GBK); -- 按照中文字母排序

--  临时表存储过程创建索引
DROP TEMPORARY TABLE IF EXISTS $GN_LXJZ_SZLXCL_9_TXT1;
CREATE TEMPORARY TABLE $GN_LXJZ_SZLXCL_9_TXT1 (INDEX INDEX1(CUSTOMER_IDENTITY)) AS
INDEX INDEX1(CUSTOMER_IDENTITY) 临时表存储过程创建索引

```

