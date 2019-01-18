package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 类型模板服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-14<p>
 */
@Service(interfaceName = "com.pinyougou.service.TypeTemplateService")
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public void save(TypeTemplate typeTemplate) {
        try{
            typeTemplateMapper.insertSelective(typeTemplate);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(TypeTemplate typeTemplate) {
        try{
            typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        try{
            // 创建Example对象
            Example example = new Example(TypeTemplate.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 添加in条件
            criteria.andIn("id", Arrays.asList(ids));
            // 条件删除
            typeTemplateMapper.deleteByExample(example);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public TypeTemplate findOne(Serializable id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<TypeTemplate> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(TypeTemplate typeTemplate, int page, int rows) {
        try{
            // 开始分页
            PageInfo<TypeTemplate> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    typeTemplateMapper.findAll(typeTemplate);
                }
            });
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


    /** 根据类型模板id查询规格选项数据 */
    public List<Map> findSpecByTypeTemplateId(Long id){
        try{
            // 1. 根据模板id查询模板对象
            TypeTemplate typeTemplate = findOne(id);

            // 2. 获取关联的规格数据
            // [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
            String specIds = typeTemplate.getSpecIds();

            // 3. 把json字符串转化成List<Map> (FastJson框架)
            List<Map> specList = JSON.parseArray(specIds, Map.class);


            // 4. 迭代规格List集合
            for (Map map : specList){
                // map: {"id":27,"text":"网络"}
                // 获取规格id
                Object specId = map.get("id");
                // SELECT * FROM `tb_specification_option` WHERE spec_id=27
                // 创建规格选项对象，封装查询条件(等于号查询条件)
                SpecificationOption so = new SpecificationOption();
                // spec_id=27
                so.setSpecId(Long.valueOf(specId.toString()));
                // 条件查询
                List<SpecificationOption> options = specificationOptionMapper.select(so);

                map.put("options", options);
            }

            // [{"id":27,"text":"网络", "options" : [{},{}]},{"id":32,"text":"机身内存","options" : [{},{}]}]
            return specList;

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
