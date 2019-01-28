package com.pinyougou;

import com.pinyougou.common.util.HttpClientUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * SmsTest
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-28<p>
 */
public class SmsTest {

    public static void main(String[] args){
        HttpClientUtils httpClientUtils = new HttpClientUtils(false);
        Map<String,String> params = new HashMap<>();
        params.put("phone", "15220321432");
        params.put("signName", "五子连珠");
        params.put("templateCode", "SMS_11480310");
        params.put("templateParam", "{'number':'888888'}");
        String content = httpClientUtils.sendPost("http://sms.pinyougou.com/sms/sendSms", params);
        System.out.println(content);
    }
}
