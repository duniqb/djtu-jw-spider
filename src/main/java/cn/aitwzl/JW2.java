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

public class JW2 {
    private String account;
    private String password;

    static CookieStore cookieStore = new BasicCookieStore();
    static HttpClient client = HttpClients.createDefault();

    HttpResponse response = null;

    public JW2(String account, String password) {
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

            code = General.getVerifyCode("D:\\img\\verifyCode.jpg");

            ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();
            postData.add(new BasicNameValuePair("groupId", null));
            postData.add(new BasicNameValuePair("j_username", account));
            postData.add(new BasicNameValuePair("j_password", password));
            postData.add(new BasicNameValuePair("j_captcha", code));

            HttpPost post = new HttpPost("http://202.199.128.21/academic/j_acegi_security_check");
            post.setEntity(new UrlEncodedFormEntity(postData));
            response = client.execute(post);


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

        System.out.println("element: " + elements.isEmpty());

        for (Element element : elements) {
            Elements tit = element.select("th");
            Elements info = element.select("td");

            for (int i = 0; i < tit.size(); i++) {
                System.out.println(tit.get(i).text() + ": " + info.get(i).text());
            }
        }
        if (!elements.isEmpty()) {
            System.out.println("================   成功   ================");
        } else {
            System.out.println("================   验证码不匹配，请重新运行！   ================");
        }


    }

    public static void main(String[] args) {
        JW2 test = new JW2("1821010431", "62052219950825133X");

        try {
            test.login();
            test.getInfo();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
