package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 品牌服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-08<p>
 */
/** interfaceName：指定服务名称 */
@Service(interfaceName = "com.pinyougou.service.BrandService")
@Transactional // 事务注解
public class BrandServiceImpl implements BrandService {

    /** 注入数据访问接口代理对象 */
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        // Dubbo会采用RPC远程调用服务
        return brandMapper.findAll();
    }
}
