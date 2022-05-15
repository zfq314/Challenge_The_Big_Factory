package com.bigdata.zfq.flink.source;

import java.sql.Timestamp;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/15 下午 4:23
 * @version: 1.0
 */

public class Event {
    public String user;
    public String url;
    public Long timestamp;

    public Event() {
    }

    public Event(String user, String url, Long timestamp) {
        this.user = user;
        this.url = url;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "user='" + user + '\'' +
                ", url='" + url + '\'' +
                ", timestamp=" + new Timestamp(timestamp) +
                '}';
    }

}
