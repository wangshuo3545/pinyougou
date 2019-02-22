package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 用户控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-28<p>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(timeout = 10000)
    private UserService userService;

    /** 用户注册 */
    @PostMapping("/save")
    public boolean save(@RequestBody User user, String code){
        System.out.println("code: " + code);
        try{
            // 检验短信验证码
            boolean success = userService.checkSmsCode(user.getPhone(), code);
            if (success) {
                userService.save(user);
            }
            return success;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 发送短信验证码 */
    @GetMapping("/sendSmsCode")
    public boolean sendSmsCode(String phone){
        try{
            return userService.sendSmsCode(phone);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

//    修改昵称和密码
    @PostMapping("/modifyPassword")
    public boolean modifyPassword(HttpServletRequest request,String password){
//        获取用户名
        String loginName = request.getRemoteUser();
        return userService.modifyPassword(loginName,password);
    }




//检查手机号码
    @GetMapping("/checkPhone")
    public boolean checkPhone (HttpServletRequest request,String checkCode){
        HttpSession session = request.getSession();
        String vcode = (String) session.getAttribute("vcode");
        System.out.println(checkCode);
        System.out.println(vcode);
        if (vcode.equalsIgnoreCase(checkCode)){
            String phone = String.valueOf(session.getAttribute("phone"));
            System.out.println(phone);
            return userService.sendSmsCode(phone);
        }
        return false;
    }


    @GetMapping("/getPhone")
    public String getPhone(HttpServletRequest request){
        String remoteUser = request.getRemoteUser();
        try {
            String phone = userService.getPhone(remoteUser);
            HttpSession session = request.getSession();
            session.setAttribute("phone", phone);
            return phone;
        } catch(Exception e){
            e.printStackTrace();
            return "号码显示错误";
        }

    }
}
