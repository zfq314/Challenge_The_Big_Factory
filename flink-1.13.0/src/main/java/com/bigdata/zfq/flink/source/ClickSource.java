package com.bigdata.zfq.flink.source;

import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Calendar;
import java.util.Random;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/15 下午 5:40
 * @version: 1.0
 */

//自定义Source
public class ClickSource implements ParallelSourceFunction<Event> {
    private Boolean runing = true;

    @Override
    public void run(SourceFunction.SourceContext<Event> sourceContext) throws Exception {
        Random random = new Random();
        String[] names = {"Mary", "Alice", "Bob", "Cary"};
        String[] urls = {"./home", "./cart", "./fav", "./prod?id=1", "./prod?id=2"};
        while (runing) {
            sourceContext.collect(new Event(names[random.nextInt(names.length)], urls[random.nextInt(urls.length)], Calendar.getInstance().getTimeInMillis()
            ));
            Thread.sleep(1000L);
        }
    }

    @Override
    public void cancel() {
        runing = false;
    }
}
