package com.pinyougou.search.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.Item;
import com.pinyougou.service.GoodsService;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 消息监听器(创建商品的索引)
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-27<p>
 */
public class ItemMessageListener implements SessionAwareMessageListener<ObjectMessage> {

    @Reference(timeout = 10000)
    private GoodsService goodsService;
    @Reference(timeout = 10000)
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        System.out.println("=========ItemMessageListener==========");
        try{
            // 1. 获取消息的内容
            Long[] goodsIds = (Long[])objectMessage.getObject();
            System.out.println("goodsIds: " + Arrays.toString(goodsIds));

            // 2. 根据goodsIds从tb_item表中查询数据
            // SELECT  * FROM `tb_item` WHERE goods_id IN(?,?,?)
            List<Item> itemList =  goodsService.findItemByGoodsId(goodsIds);

            // 3. 把List<Item> 转化成 List<SolrItem>
            List<SolrItem> solrItems = new ArrayList<>();
            for (Item item1 : itemList){
                // 把Item 转化成SolrItem
                SolrItem solrItem = new SolrItem();
                solrItem.setId(item1.getId());
                solrItem.setTitle(item1.getTitle());
                solrItem.setPrice(item1.getPrice());
                solrItem.setImage(item1.getImage());
                solrItem.setGoodsId(item1.getGoodsId());
                solrItem.setCategory(item1.getCategory());
                solrItem.setBrand(item1.getBrand());
                solrItem.setSeller(item1.getSeller());
                solrItem.setUpdateTime(item1.getUpdateTime());

                // spec {"网络":"联通4G","机身内存":"64G"}
                Map<String,String> specMap = JSON.parseObject(item1.getSpec(), Map.class);
                // 设置动态域
                solrItem.setSpecMap(specMap);
                solrItems.add(solrItem);
            }

            // 4. 调用搜索服务接口同步索引数据
            itemSearchService.saveOrUpdate(solrItems);

            // 提交事务
            session.commit();
        }catch (Exception ex){
            // 回滚事务
            session.rollback();
            throw new RuntimeException(ex);
        }
    }
}
