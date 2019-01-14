package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 品牌控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-08<p>
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    /**
     * 引用服务
     * 调用服务方法超时的时间 1000毫秒
     * */
    @Reference(timeout = 10000)
    private BrandService brandService;

    /** 分页查询品牌 */
    @GetMapping("/findByPage")
    public PageResult findByPage(Brand brand, Integer page, Integer rows){
        try {
            // GET请求转码
            //  StringUtils.isNoneBlank 判断字符串是不是空 null 、 ""、 " "
            if (brand != null && StringUtils.isNoneBlank(brand.getName())) {
                brand.setName(new String(brand.getName().getBytes("ISO8859-1"), "UTF-8"));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        // {total : 100, rows : [{},{}]} map、实体
        return brandService.findByPage(brand,page,rows);
    }

    /** 添加品牌 */
    @PostMapping("/save")
    public boolean save(@RequestBody Brand brand){
        try{
            brandService.save(brand);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 修改品牌 */
    @PostMapping("/update")
    public boolean update(@RequestBody Brand brand){
        try{
            brandService.update(brand);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 删除品牌 */
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try{
            brandService.deleteAll(ids);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 查询品牌列表 */
    @GetMapping("/findBrandList")
    public List<Map<String,Object>> findBrandList(){
        // 响应数据: [{id : 1, text : '华为'},{id: 2 , text : '小米'}]
        return brandService.findAllByIdAndName();
    }

}
