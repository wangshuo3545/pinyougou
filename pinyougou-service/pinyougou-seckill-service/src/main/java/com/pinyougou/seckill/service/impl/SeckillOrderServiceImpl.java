package com.pinyougou.seckill.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 秒杀订单服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-02-18<p>
 */
@Service(interfaceName = "com.pinyougou.service.SeckillOrderService")
@Transactional
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public void save(SeckillOrder seckillOrder) {

    }

    @Override
    public void update(SeckillOrder seckillOrder) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SeckillOrder findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SeckillOrder> findAll() {
        return null;
    }

    @Override
    public List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows) {
        return null;
    }

    /**
     * 提交订单到Redis数据库
     * synchronized: 线程锁  (单进程)
     * 分布式锁(Redis、mysql、zookeeper) (多进程)
     *
     * key : value
     * a  true
     * Mysql数据库 事务引擎如果是innodb 行级锁
     * */
    public synchronized boolean submitOrderRedis(String userId, Long id){
        try{
            // 1. 从Redis数据库获取秒杀商品
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate
                    .boundHashOps("seckillGoodsList").get(id);


            // 2. 判断秒杀商品的剩余库存数量
            if (seckillGoods != null && seckillGoods.getStockCount() > 0) {
                // 3. 减库存
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);

                // 4. 再判断剩余库存数量，如果是零，把秒杀商品同步到数据库，从Redis中删除该秒杀商品，不是零，把秒杀商品重新存储到Redis
                if (seckillGoods.getStockCount() == 0){ // 秒光了
                    // 把秒杀商品同步到数据库
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                    // 从Redis中删除该秒杀商品
                    redisTemplate.boundHashOps("seckillGoodsList").delete(id);
                }else{
                    // 不是零，把秒杀商品重新存储到Redis
                    redisTemplate.boundHashOps("seckillGoodsList").put(id, seckillGoods);
                }

                // 5. 产生秒杀预订单
                SeckillOrder seckillOrder = new SeckillOrder();
                // 秒杀订单id
                seckillOrder.setId(idWorker.nextId());
                // 秒杀商品id
                seckillOrder.setSeckillId(id);
                // 秒杀订单金额
                seckillOrder.setMoney(seckillGoods.getCostPrice());
                // 用户id
                seckillOrder.setUserId(userId);
                // 商家id
                seckillOrder.setSellerId(seckillGoods.getSellerId());
                // 订单创建时间
                seckillOrder.setCreateTime(new Date());
                // 支付状态码
                seckillOrder.setStatus("0");

                // 6. 把秒杀预订单存入Redis
                redisTemplate.boundHashOps("seckillOrderList").put(userId, seckillOrder);

                return true;
            }
            return false;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 查询秒杀查询预订单 */
    public SeckillOrder findSeckillOrderFromRedis(String userId){
        try{
            return (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(userId);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 支付成功,订单存储到mysql数据库 */
    public void saveOrder(String userId, String transactionId){
        try{
            // 1. 获取Redis中的秒杀订单
            SeckillOrder seckillOrder = findSeckillOrderFromRedis(userId);

            // 2. 修改属性
            seckillOrder.setStatus("1"); // 支付成功
            // 支付时间
            seckillOrder.setPayTime(new Date());
            // 微信支付系统的订单号
            seckillOrder.setTransactionId(transactionId);

            // 3. 同步到数据库
            seckillOrderMapper.insertSelective(seckillOrder);

            // 4. 从Redis数据库删除该秒杀订单
            redisTemplate.boundHashOps("seckillOrderList").delete(userId);

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
