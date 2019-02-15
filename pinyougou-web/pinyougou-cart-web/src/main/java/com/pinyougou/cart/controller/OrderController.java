package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Order;
import com.pinyougou.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 订单控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-02-15<p>
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(timeout = 10000)
    private OrderService orderService;

    /** 保存订单 */
    @PostMapping("/save")
    public boolean save(@RequestBody Order order, HttpServletRequest request){
        try{
            // 获取登录用户名
            String userId = request.getRemoteUser();
            // 设置订单关联的用户
            order.setUserId(userId);
            // 调用服务层接口
            orderService.save(order);

            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
