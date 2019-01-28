package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-28<p>
 */
@Service(interfaceName = "com.pinyougou.service.UserService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${sms.url}")
    private String smsUrl;
    @Value("${sms.signName}")
    private String signName;
    @Value("${sms.templateCode}")
    private String templateCode;

    @Override
    public void save(User user) {
        try{
            // 密码加密 commons-codec.jar
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            // 创建时间
            user.setCreated(new Date());
            // 修改时间
            user.setUpdated(user.getCreated());
            userMapper.insertSelective(user);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public User findOne(Serializable id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public List<User> findByPage(User user, int page, int rows) {
        return null;
    }

    /** 发送短信验证码  */
    public boolean sendSmsCode(String phone){
        try{
            // 1. 生成随机的六位数字验证码
            String code = UUID.randomUUID().toString().replaceAll("-","")
                    .replaceAll("[a-z|A-Z]","").substring(0,6);
            System.out.println("验证码：" + code);

            // 2. 调用短信发送接口(HttpClientUtils)
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            System.out.println("signName: " + signName);
            // 定义Map集合封装请求参数
            Map<String,String> params = new HashMap<>();
            params.put("phone", phone);
            params.put("signName", signName);
            params.put("templateCode", templateCode);
            params.put("templateParam", "{'number':'"+ code +"'}");
            String content = httpClientUtils.sendPost(smsUrl, params);

            System.out.println(content);
            // content: {"success" : true}
            Map<String,Object> resMap = JSON.parseObject(content, Map.class);

            // 3. 判断短信是否发送成功
            boolean success = (boolean)resMap.get("success");
            if (success){
                // 4. 存储验证码到Redis数据库(发送成功) 指定key的有效时间 90秒
                redisTemplate.boundValueOps(phone).set(code, 90, TimeUnit.SECONDS);
            }

            // 5. 返回成功还失败
            return success;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 检验短信验证码 */
    public boolean checkSmsCode(String phone, String code){
        try{
            // 从Redis数据库获取验证码
            String oldCode = (String) redisTemplate.boundValueOps(phone).get();
            return code.equals(oldCode);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


}
