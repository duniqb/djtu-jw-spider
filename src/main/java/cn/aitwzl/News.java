package cn.aitwzl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.net.URL;
import java.util.List;

public class News implements PageProcessor {

    @Override
    public void process(Page page) {
        List<Selectable> list = page.getHtml().css("div#thirdcontent ul.articleList li").nodes();

        // 判断集合是否为空
        if (list.size() == 0) {
            // 为空是详情页，执行解析详情方法
            try {
                this.parseDetail(page);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 不为空是列表页，解析url，放入任务队列
            for (Selectable selectable : list) {
                String detailUrl = selectable.links().toString();
                page.addTargetRequest(detailUrl);
            }

            // 获取下一页url，放入队列
            String bkUrl = page.getHtml().css("span.classicLookPaging li").nodes().get(7).links().toString();
            page.addTargetRequest(bkUrl);
        }
    }

    /**
     * 解析详情页
     *
     * @param page
     * @throws Exception
     */
    int i = 1;

    private void parseDetail(Page page) throws Exception {

        String title = page.getHtml().css("div#article h2", "text").toString();
        String date = page.getHtml().css("div#articleInfo ul li", "text").toString();

        Document doc = Jsoup.parse(new URL(page.getUrl().toString()), 1000);
        String article = doc.select("div#article div.body input").attr("value");

        i += 1;

        System.out.println("第 " + i + " 条抓取成功！");
        System.out.println(title);
        System.out.println(date);
//        文章无法解析，记录链接点击
        System.out.println("详情页链接：" + page.getUrl());

        System.out.println(article);
        System.out.println();
    }

    private Site site = Site.me()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36")
            .setTimeOut(10000)
            .setRetryTimes(3)
            .setRetrySleepTime(3000);

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String url = "http://202.199.128.21/homepage/infoArticleList.do?sortColumn=publicationDate&pagingNumberPer=10&columnId=10182&sortDirection=-1&pagingPage=1&";

        Spider.create(new News()).thread(2).addUrl(url).run();
    }
}
