package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-19<p>
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    /** 多条件分页查询待审核的商品列表 */
    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows){
        try{
            // 设置审核状态: 未审核
            goods.setAuditStatus("0");

            // get请求转码
            if (goods != null && StringUtils.isNoneBlank(goods.getGoodsName())){
                goods.setGoodsName(new String(goods.getGoodsName().getBytes("ISO8859-1"), "UTF-8"));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return goodsService.findByPage(goods, page, rows);
    }

    /** 修改商品审核状态 */
    @GetMapping("/updateStatus")
    public boolean updateStatus(Long[] ids, String status){
        try{
            goodsService.updateStatus("audit_status", ids, status);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 删除商品(修改商品删除状态) */
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try{
            goodsService.updateStatus("is_delete", ids, "1");
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
