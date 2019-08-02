package cn.aitwzl.dj.utils;

import cn.aitwzl.dj.utils.AuthService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.ai.aip.utils.FileUtil;
import com.baidu.ai.aip.utils.Base64Util;
import com.baidu.ai.aip.utils.HttpUtil;


import java.net.URLEncoder;

/**
 * OCR 通用识别
 */
public class General {

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static void main(String[] args) {

        // 通用识别url
//        String otherHost = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        String otherHost = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";
        // 本地图片路径
        String filePath = "D:\\img\\test.jpg";
        try {
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String params = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imgStr, "UTF-8");
            /**
             * 线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
             */
            String accessToken = AuthService.getAuth();
            String result = HttpUtil.post(otherHost, accessToken, params);
            System.out.println(result);
            //将json字符串转换成jsonObject对象
            JSONObject jsonObject = JSON.parseObject(result);
            //取出words_result对象中words_result对应数组中第一个json子对象下面words对应的value值
            System.out.println(jsonObject.getJSONArray("words_result").getJSONObject(0).get("words"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("方法：" + getVerifyCode(filePath));

    }

    /**
     * 根据文件路径识别并返回验证码
     * @param filePath 图片路径
     * @return 识别的字符串
     */
    public static String getVerifyCode(String filePath) {
        // 通用识别url
        String otherHost = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
//        String otherHost = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";

        try {
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String params = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imgStr, "UTF-8");
            /**
             * 线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
             */
            String accessToken = AuthService.getAuth();
            String result = HttpUtil.post(otherHost, accessToken, params);

            //将json字符串转换成jsonObject对象
            JSONObject jsonObject = JSON.parseObject(result);
            //取出words_result对象中words_result对应数组中第一个json子对象下面words对应的value值

            String rst = (String)jsonObject.getJSONArray("words_result").getJSONObject(0).get("words");
            System.out.println("验证码： " + rst);

            return rst;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
