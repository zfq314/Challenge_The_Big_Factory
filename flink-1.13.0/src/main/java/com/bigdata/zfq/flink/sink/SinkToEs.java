package com.bigdata.zfq.flink.sink;

import com.bigdata.zfq.flink.source.ClickSource;
import com.bigdata.zfq.flink.source.Event;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/18 上午 10:35
 * @version: 1.0
 */

public class SinkToEs {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Event> click = env.addSource(new ClickSource());
        ArrayList<HttpHost> list = new ArrayList();

        // http.port: 9200
        // 设置对外服务的http端口，默认为9200。 hadoop2.x 此端口已经占用
        list.add(new HttpHost("hadoop31", 9200, "http"));

        // 创建一个ElasticsearchSinkFunction
        ElasticsearchSinkFunction<Event> elasticsearchSinkFunction = new ElasticsearchSinkFunction<Event>() {
            @Override
            public void process(Event event, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
                HashMap<String, String> data = new HashMap<>();
                data.put(event.user, event.url);
                //org.elasticsearch.client.Requests
                IndexRequest indexRequest = Requests.indexRequest()
                        .index("clicks")
                        .type("type")// es6 需要指定type
                        .source(data);
                requestIndexer.add(indexRequest);
            }
        };
        click.addSink(new ElasticsearchSink.Builder<Event>(list, elasticsearchSinkFunction).build());

        env.execute();
    }
}
