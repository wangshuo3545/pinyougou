package com.pinyougou.service;

import java.util.Map; /**
 * 商品搜索服务接口
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-23<p>
 */
public interface ItemSearchService {

    /**
     * 商品搜索方法
     * @param params 搜索条件
     * @return 数据
     */
    Map<String,Object> search(Map<String, Object> params);
}
