package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
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
    public void save(Brand brand) {
        brandMapper.insertSelective(brand);
    }

    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Brand findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Brand> findAll() {

//        // 开始分页
//        PageInfo<Brand> pageInfo = PageHelper.startPage(1, 5)
//                .doSelectPageInfo(new ISelect() {
//            @Override
//            public void doSelect() {
//                // Dubbo会采用RPC远程调用服务
//                brandMapper.selectAll();
//            }
//        });
//
//        System.out.println("总记录数：" + pageInfo.getTotal());
//        System.out.println("总页数：" + pageInfo.getPages());
//        System.out.println("分页的数据：" + pageInfo.getList());

        return brandMapper.selectAll();
    }

    @Override
    public List<Brand> findByPage(Brand brand, int page, int rows) {
        return null;
    }
}
