package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-02-15<p>
 */
@Service(interfaceName = "com.pinyougou.service.OrderService")
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(Order order) {
        try{
            // 1. 获取用户的购物车数据
            List<Cart> carts = (List<Cart>)redisTemplate
                    .boundValueOps("cart_" + order.getUserId()).get();

            // 2. 往订单表插入数据
            // 一个商家生成一个订单 (cart)
            for (Cart cart : carts) {
                // 创建订单
                Order order1 = new Order();
                // 用分布式id生成器生成订单id
                long orderId = idWorker.nextId();
                // 订单id
                order1.setOrderId(orderId);
                // 支付方式
                order1.setPaymentType(order.getPaymentType());
                // 1、未付款
                order1.setStatus("1");
                // 订单创建时间
                order1.setCreateTime(new Date());
                // 用户id
                order1.setUserId(order.getUserId());
                // 收件人地址
                order1.setReceiverAreaName(order.getReceiverAreaName());
                // 收件人手机
                order1.setReceiverMobile(order.getReceiverMobile());
                // 收件人姓名
                order1.setReceiver(order.getReceiver());
                // 订单来源
                order1.setSourceType(order.getSourceType());
                // 订单关联的商家id
                order1.setSellerId(cart.getSellerId());

                double money = 0;

                // 3. 往订单明细表插入数据
                for (OrderItem orderItem : cart.getOrderItems()) {
                    // 设置订单明细的主键id
                    orderItem.setId(idWorker.nextId());
                    // 设置关联的订单
                    orderItem.setOrderId(orderId);

                    // 累计商品的金额
                    money += orderItem.getTotalFee().doubleValue();
                    // 往tb_order_item表插入数据
                    orderItemMapper.insertSelective(orderItem);
                }


                // 订单总金额
                order1.setPayment(new BigDecimal(money));
                // 往tb_order表插入数据
                orderMapper.insertSelective(order1);
            }

            // 4. 删除购物车数据
            redisTemplate.delete("cart_" + order.getUserId());


        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Order order) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Order findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public List<Order> findByPage(Order order, int page, int rows) {
        return null;
    }
}
