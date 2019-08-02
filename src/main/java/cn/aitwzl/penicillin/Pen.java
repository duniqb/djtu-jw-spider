package cn.aitwzl.penicillin;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.UUID;

public class Pen implements PageProcessor {

    private int i = 1;

    @Override
    public void process(Page page) {
        String url = "https://www.penicillin.cn";

        handle();
        for (int j = 0; j < 5; j++) {
            String newUrl = url + "/" + UUID.randomUUID().toString();
            System.out.println(newUrl);
            page.addTargetRequest(newUrl);
        }
        System.out.println("added");
    }

    private void handle() {
        System.out.println("第 " + i + " 次执行...");
    }

    private Site site = Site.me()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36")
            .setTimeOut(10000)
            .setRetrySleepTime(3000);

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String url = "https://www.penicillin.cn";
        Spider.create(new Pen()).thread(2).addUrl(url).start();
    }
}
