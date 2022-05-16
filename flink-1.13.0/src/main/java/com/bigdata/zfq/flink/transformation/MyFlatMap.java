package com.bigdata.zfq.flink.transformation;

import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/16 下午 10:52
 * @version: 1.0
 */

//另外，富函数类提供了getRuntimeContext()方法（我们在本节的第一个例子中使用了一下），
// 可以获取到运行时上下文的一些信息，例如程序执行的并行度，任务名称，以及状态（state）。
// 这使得我们可以大大扩展程序的功能，特别是对于状态的操作，使得Flink中的算子具备了处理复杂业务的能力。
// 关于Flink中的状态管理和状态编程，我们会在后续章节逐渐展开。

//RichFunction是抽象类 不是接口，新增了对象的生命周期
public class MyFlatMap extends RichFlatMapFunction<Event, String> {

    @Override
    public void open(Configuration parameters) throws Exception {
        // 做一些初始化工作
        // 例如建立一个和MySQL的连接
        super.open(parameters);
    }

    @Override
    public void flatMap(Event value, Collector<String> out) throws Exception {
        // 对数据库进行读写
    }

    @Override
    public void close() throws Exception {
        super.close();
        // 清理工作，关闭和MySQL数据库的连接。
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return super.getRuntimeContext();
    }
}
