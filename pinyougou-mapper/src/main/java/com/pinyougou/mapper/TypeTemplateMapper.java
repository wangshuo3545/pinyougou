package com.pinyougou.mapper;

import com.pinyougou.pojo.TypeTemplate;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * TypeTemplateMapper 数据访问接口
 * @date 2019-01-11 09:53:21
 * @version 1.0
 */
public interface TypeTemplateMapper extends Mapper<TypeTemplate>{

    /** 多条件查询类型模板 */
    List<TypeTemplate> findAll(TypeTemplate typeTemplate);

}