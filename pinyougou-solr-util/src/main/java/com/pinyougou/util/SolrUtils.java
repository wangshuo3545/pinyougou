package com.pinyougou.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.solr.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SolrUtils
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-23<p>
 */
@Component
public class SolrUtils {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    /** 把tb_item表的数据导入到Solr服务器的collection1索引库 */
    public void importDataToSolr(){

        // 1. 查询SKU表中的数据
        // 1.1 状态码 1
        Item item = new Item();
        // 正常的商品
        item.setStatus("1");
        // 1.2 查询数据
        List<Item> itemList = itemMapper.select(item);

        // 创建SolrItem集合
        List<SolrItem> solrItems = new ArrayList<>();

        System.out.println("========华丽丽分隔线==========");
        for (Item item1 : itemList){
            System.out.println(item1.getId() + "\t" + item1.getTitle());
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


        // 添加或修改索引库(修改索引与文档)
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        if (updateResponse.getStatus() == 0){
            solrTemplate.commit();
        }else{
            solrTemplate.rollback();
        }

        System.out.println("========华丽丽分隔线==========");
    }

    public static void main(String[] args){
        // 创建Spring容器
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        // 获取SolrUtils
        SolrUtils solrUtils = ac.getBean(SolrUtils.class);
        // 调用方法
        solrUtils.importDataToSolr();
    }

}
