package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 商品详情控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-26<p>
 */
@Controller
public class ItemController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    /**
     * http://item.pinyougou.com/3694111.html
     * {goodsId}: 定义请求URL中的变量
     */
    @GetMapping("/{goodsId}")
    public String getGoods(@PathVariable("goodsId") Long goodsId, Model model){

        System.out.println("goodsId: " + goodsId);
        // model : 数据模型
        // 根据SPU商品的id 查询商品数据
        Map<String,Object> dataModel = goodsService.getGoods(goodsId);
        // 把数据添加到数据模型
        model.addAllAttributes(dataModel);

        return "item";
    }
}
