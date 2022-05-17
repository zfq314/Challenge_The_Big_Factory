package com.bigdata.zfq.flink.transformation;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/17 上午 11:30
 * @version: 1.0
 */

public class RescalePartitioning {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 这里使用了并行数据源的富函数版本
        // 这样可以调用getRuntimeContext方法来获取运行时上下文的一些信息
        env.addSource(new RichParallelSourceFunction<Integer>() {
            @Override
            public void run(SourceContext<Integer> ctx) throws Exception {
                // 将奇数发送到索引为1的并行子任务
                // 将偶数发送到索引为0的并行子任务
                for (int i = 0; i <8; i++) {
                    if ((i + 1) % 2 == getRuntimeContext().getIndexOfThisSubtask()) {
                        ctx.collect(i + 1);
                    }

                }
            }

            @Override
            public void cancel() {

            }
        }).setParallelism(2).rescale().print("rescale").setParallelism(4);

        env.execute();
    }
}
