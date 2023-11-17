package com.cnwy.crawler.util;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.Map;

@Slf4j
public class DingTalk {
    public static void main(String[] args) {
        send("xxxx");
    }
    public static void send(String msgInfo) {
        try {
            Map<String, Object> msg = Map.of("msgtype", "text", "text", Map.of("content", "临时机器人:"+msgInfo));
            String body = Jsoup.connect("https://oapi.dingtalk.com/robot/send?access_token=0b77cc4cdb6492ab61d1698f332a5789d9da289ccdf345d019f186a93bf4b168")
                    .header("Content-Type", "application/json")
                    .requestBody(JSON.toJSONString(msg))
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .execute()
                    .body();
            log.info("钉钉临时群发送消息结果:{}", body);
        } catch (Exception e) {
            log.error("钉钉临时群发送消息失败!", e);
        }
    }
}
