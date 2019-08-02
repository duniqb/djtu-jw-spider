package cn.aitwzl;

import cn.aitwzl.dj.utils.General;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class JW {
    private String account;
    private String password;

    static CookieStore cookieStore = new BasicCookieStore();
    static HttpClient client = HttpClients.createDefault();

    HttpResponse response = null;

    public JW(String account, String password) {
        super();
        this.account = account;
        this.password = password;
    }

    /**
     * 登录
     */
    public void login() {
        HttpGet getLoginPage = new HttpGet("http://202.199.128.21/academic/common/security/login.jsp");

        getLoginPage.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        getLoginPage.setHeader("Accept-Encoding", "gzip, deflate");
        getLoginPage.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        getLoginPage.setHeader("Connection", "keep-alive");
        getLoginPage.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");

        try {
            String code;
            client.execute(getLoginPage);

            getVerifyCode(client);
            // 指定本地的图片路径
            code = General.getVerifyCode("D:\\img\\verifyCode.jpg");

            ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();
            postData.add(new BasicNameValuePair("groupId", null));
            postData.add(new BasicNameValuePair("j_username", account));
            postData.add(new BasicNameValuePair("j_password", password));
            postData.add(new BasicNameValuePair("j_captcha", code));

            HttpPost post = new HttpPost("http://202.199.128.21/academic/j_acegi_security_check");
            post.setEntity(new UrlEncodedFormEntity(postData));


            response = client.execute(post);
            System.out.println("response: " + response.toString());
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println("char: " + response.toString().charAt(106));

        } catch (ClientProtocolException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 获取验证码
     *
     * @param client
     */
    public void getVerifyCode(HttpClient client) {
        HttpGet getVerifyCode = new HttpGet("http://202.199.128.21/academic/getCaptcha.do");
        FileOutputStream fileOutputStream = null;
        HttpResponse response;

        try {
            response = client.execute(getVerifyCode);
            // 指定保存到本地的图片路径
            fileOutputStream = new FileOutputStream(new File("D:\\img\\verifyCode.jpg"));
            response.getEntity().writeTo(fileOutputStream);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 输出个人信息
     *
     * @throws Exception
     */
    public void getInfo() throws Exception {
        HttpResponse response = client.execute(new HttpGet("http://202.199.128.21/academic/showPersonalInfo.do"));
        Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));

        doc.setBaseUri("http://202.199.128.21/");
        Elements elements = doc.select("table.form tr");

        for (Element element : elements) {
            Elements tit = element.select("th");

            Elements info = element.select("td");

            for (int i = 0; i < tit.size(); i++) {
                System.out.println(tit.get(i).text() + ": " + info.get(i).text());
            }
        }

        System.out.println("================   成功   ================");
    }

    /**
     * 获取学年的学期并输出
     */
    public void getScore() {
        parse(getScoreParm("38", "2"));
    }

    /**
     * 根据学年学期查询成绩
     *
     * @param year
     * @param term
     * @return
     */
    public HttpResponse getScoreParm(String year, String term) {
        try {
            HttpPost post = new HttpPost("http://202.199.128.21/academic/manager/score/studentOwnScore.do?groupId=&moduleId=2020");
            ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();

            postData.add(new BasicNameValuePair("year", year));
            postData.add(new BasicNameValuePair("term", term));
            postData.add(new BasicNameValuePair("para", "0"));
            postData.add(new BasicNameValuePair("sortColumn", ""));
            postData.add(new BasicNameValuePair("Submit", "查询"));

            post.setEntity(new UrlEncodedFormEntity(postData));

            HttpResponse response = client.execute(post);
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据学期查询课表
     */
    public HttpResponse getTimeTable(int year, int term) {
        try {
            String url = "http://202.199.128.21/academic/student/currcourse/currcourse.jsdo?";

            // 2.输入网址,发起get请求创建HttpGet对象
            HttpGet httpGet = new HttpGet(url + "year=" + year + "&term=" + term);

            // 3.返回响应
            HttpResponse response = client.execute(httpGet);

            Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
            Element element = doc.select("table.infolist_tab").first();

            // 标题集合
            Elements th = element.select("tr th");
            // 总行数
            Elements tr = element.select("tr.infolist_common");

            for (int i = 0; i < tr.size(); i++) {
                Elements trd = tr.get(i).select("td");
                for (int j = 0; j < th.size() - 2; j++) {
                    System.out.println(th.get(j).text() + ": " + trd.get(j).text());
                }
                System.out.println("-------------------------------------------------");
            }

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void parse(HttpResponse response) {
        try {
            Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
            Element element = doc.select("table.datalist").first();

            Elements th = element.select("th");

            Elements td = element.select("tr");

            for (int i = 1; i < td.size(); i++) {
                Elements trd = td.get(i).select("td");
                for (int j = 0; j < trd.size(); j++) {
                    System.out.println(th.get(j).text() + ": " + trd.get(j).text());
                }
                System.out.println("-------------------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // 填入学号和密码
        JW test = new JW("学号", "密码");

        try {
            test.login();
            test.getInfo();
            test.getScore();
            test.getTimeTable(38, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
