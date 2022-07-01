package com.bigdata.zfq.flink.cep;

import com.bigdata.zfq.flink.pojo.LoginEvent;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;
import java.util.Map;

/**
 * @ClassName LoginFailDetect
 * @Description TODO
 * @Author ZFQ
 * @Date 2022/7/1 13:53
 * @Version 1.0
 **/
//模式中的每个简单事件，会用一个.where()方法来指定一个约束条件，指明每个事件的特征，这里就是eventType为“fail”。
//而模式里表示事件之间的关系时，使用了.next()方法。next是“下一个”的意思，表示紧挨着、中间不能有其他事件（比如登录成功），这是一个严格近邻关系。第一个事件用.begin()方法表示开始。所有这些“连接词”都可以有一个字符串作为参数，这个字符串就可以认为是当前简单事件的名称。所以我们如果检测到一组匹配的复杂事件，里面就会有连续的三个登录失败事件，它们的名称分别叫作“first”“second”和“third”。
//在第三步处理复杂事件时，调用了PatternStream的.select()方法，传入一个PatternSelectFunction对检测到的复杂事件进行处理。而检测到的复杂事件，会放在一个Map中；PatternSelectFunction内.select()方法有一个类型为 Map<String, List<LoginEvent>>的参数map，里面就保存了检测到的匹配事件。这里的key是一个字符串，对应着事件的名称，而value是LoginEvent的一个列表，匹配到的登录失败事件就保存在这个列表里。最终我们提取userId和三次登录的时间戳，包装成字符串输出一个报警信息。
//运行代码可以得到结果如下：
//warning>user_1 连续三次登录失败！登录时间：2000,3000,5000
//可以看到，user_1连续三次登录失败被检测到了；而user_2尽管也有三次登录失败，但中间有一次登录成功，所以不会被匹配到。

public class LoginFailDetect {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);
        //获取登录事件，并提取时间戳，生成水位线
        KeyedStream<LoginEvent, String> stream = executionEnvironment.fromElements(
                new LoginEvent("user_1", "192.168.0.1", "fail", 2000L),
                new LoginEvent("user_1", "192.168.0.2", "fail", 3000L),
                new LoginEvent("user_2", "192.168.1.29", "fail", 4000L),
                new LoginEvent("user_1", "171.56.23.10", "fail", 5000L),
                new LoginEvent("user_2", "192.168.1.29", "success", 6000L),
                new LoginEvent("user_2", "192.168.1.29", "fail", 7000L),
                new LoginEvent("user_2", "192.168.1.29", "fail", 8000L)

        ).assignTimestampsAndWatermarks(WatermarkStrategy.<LoginEvent>forMonotonousTimestamps().withTimestampAssigner(new SerializableTimestampAssigner<LoginEvent>() {
            @Override
            public long extractTimestamp(LoginEvent element, long recordTimestamp) {
                return element.timestamp;
            }
        })).keyBy(r -> r.userId);

        //定义pattern连续的3个登录失败事件
        Pattern<LoginEvent, LoginEvent> pattern = Pattern.<LoginEvent>begin("first").where(new SimpleCondition<LoginEvent>() {
            @Override
            public boolean filter(LoginEvent value) throws Exception {
                return value.eventType.equals("fail");
            }
        })//接着第二个登录失败事件
                .next("second")
                .where(new SimpleCondition<LoginEvent>() {
                    @Override
                    public boolean filter(LoginEvent value) throws Exception {
                        return value.eventType.equals("fail");
                    }
                })//接着是第三个登录失败的事件
                .next("third")
                .where(new SimpleCondition<LoginEvent>() {
                    @Override
                    public boolean filter(LoginEvent value) throws Exception {
                        return value.eventType.equals("fail");
                    }
                });
        //将pattern应用到流上，检测匹配的复杂事件，得到一个patternStream
        PatternStream<LoginEvent> patternStream = CEP.pattern(stream, pattern);
        //将匹配到的事件选择出来，然后包装成字符串打印输出
        patternStream.select(new PatternSelectFunction<LoginEvent, String>() {
            @Override
            public String select(Map<String, List<LoginEvent>> map) throws Exception {
                LoginEvent first = map.get("first").get(0);
                LoginEvent second = map.get("second").get(0);
                LoginEvent third = map.get("third").get(0);
                return first.userId + " 连续三次登录失败！登录时间：" + first.timestamp + ", " + second.timestamp + ", " + third.timestamp;
            }
        }).print("warnning");

        executionEnvironment.execute();
    }
}
