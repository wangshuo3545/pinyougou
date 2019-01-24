package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品搜索服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-23<p>
 */
@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
public class ItemSearchServiceImpl implements ItemSearchService{

    @Autowired
    private SolrTemplate solrTemplate;
    /**
     * 商品搜索方法
     * @param params 搜索条件
     * @return 数据
     */
    public Map<String,Object> search(Map<String, Object> params){
        try{
            Map<String,Object> data = new HashMap<>();

            // 获取关键字
            String keywords = (String)params.get("keywords");

            // 获取分页参数  page : 1, rows : 15
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            if (page == null || page < 1){
                page = 1;
            }
            if (rows == null || rows < 1){
                rows = 20;
            }


            // 判断关键字是否为空
            if (StringUtils.isNoneBlank(keywords)){ // 如果关键字不为空，高亮查询

                // 创建高亮查询对象
                HighlightQuery highlightQuery = new SimpleHighlightQuery();
                // 创建条件对象
                Criteria criteria = new Criteria("keywords").is(keywords);
                // 添加条件
                highlightQuery.addCriteria(criteria);

                // 创建高亮选项对象(设置高亮选项参数)
                HighlightOptions highlightOptions = new HighlightOptions();
                // 设置哪个域中出现了关键字需要高亮显示
                highlightOptions.addField("title");
                // 设置高亮格式器前缀
                highlightOptions.setSimplePrefix("<font color='red'>");
                // 设置高亮格式器后缀
                highlightOptions.setSimplePostfix("</font>");

                // 添加高亮选项对象
                highlightQuery.setHighlightOptions(highlightOptions);


                // params: {"keywords":"小米","category":"手机","brand":"苹果",
                //          "price":"1500-2000","spec":{"网络":"联通3G","机身内存":"128G"}}
                // 1.按商品分类过滤 (category)
                String category = (String)params.get("category");
                if (StringUtils.isNoneBlank(category)){
                    // 创建过滤条件
                    Criteria criteria1 = new Criteria("category").is(category);
                    // 添加过滤查询
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }

                // 2.按商品品牌过滤 (brand)
                String brand = (String)params.get("brand");
                if (StringUtils.isNoneBlank(brand)){
                    // 创建过滤条件
                    Criteria criteria1 = new Criteria("brand").is(brand);
                    // 添加过滤查询
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }

                // 3.按商品规格过滤 (spec_*) spec_网络|spec_机身内存
                // {"网络":"联通3G","机身内存":"128G"}
                Map<String,String> specMap = (Map<String, String>) params.get("spec");
                for (String key : specMap.keySet()){
                    // 创建过滤条件
                    Criteria criteria1 = new Criteria("spec_" + key).is(specMap.get(key));
                    // 添加过滤查询
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }


                // 4.按商品价格过滤 (price)
                // 0-500  1500-2000  3000-*
                String price = (String)params.get("price");
                if (StringUtils.isNoneBlank(price)){
                    // 得到价格数组
                    String[] priceArr = price.split("-");
                    // 判断起始价格不是0
                    if (!"0".equals(priceArr[0])){
                        // 创建过滤条件
                        Criteria criteria1 = new Criteria("price").greaterThanEqual(priceArr[0]);
                        // 添加过滤查询
                        highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                    }
                    // 判断结束价格不是*
                    if (!"*".equals(priceArr[1])){
                        // 创建过滤条件
                        Criteria criteria1 = new Criteria("price").lessThanEqual(priceArr[1]);
                        // 添加过滤查询
                        highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                    }
                }


                // 设置分页起始记录数
                highlightQuery.setOffset((page - 1) * rows);
                // 设置页大小
                highlightQuery.setRows(rows);


                // 搜索排序 sortField : '', sortValue : ''
                // 获取排序参数
                String sortField = (String)params.get("sortField");
                String sortValue = (String)params.get("sortValue");
                if (StringUtils.isNoneBlank(sortField) && StringUtils.isNoneBlank(sortValue)){
                    // 创建排序对象
                    Sort sort = new Sort("ASC".equals(sortValue) ?
                            Sort.Direction.ASC : Sort.Direction.DESC, sortField);
                    // 添加排序对象
                    highlightQuery.addSort(sort);
                }



                // 高亮分页查询，得到高亮分页对象
                HighlightPage<SolrItem> highlightPage = solrTemplate
                        .queryForHighlightPage(highlightQuery, SolrItem.class);

                // 获取高亮集合对象
                List<HighlightEntry<SolrItem>> highlighted = highlightPage.getHighlighted();
                // 迭代高亮集合对象
                for (HighlightEntry<SolrItem> highlightEntry : highlighted){
                    // 获取SolrItem
                    SolrItem solrItem = highlightEntry.getEntity();
                    // 获取高亮内容集合
                    List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
                    // 判断高亮内容集合
                    if (highlights != null && highlights.size() > 0){
                        // 获取集合的元素 highlight
                        HighlightEntry.Highlight highlight = highlights.get(0);
                        // 获取title标题的高亮的内容
                        String title = highlight.getSnipplets().get(0).toString();
                        System.out.println("title: " + title);
                        solrItem.setTitle(title);
                    }
                }

                // 获取分页数据
                List<SolrItem> solrItemList = highlightPage.getContent();

                // 设置分页数据
                data.put("rows", solrItemList);
                // 设置总页数
                data.put("totalPages", highlightPage.getTotalPages());
                // 设置总记录数
                data.put("total", highlightPage.getTotalElements());

            }else{ // 简单查询

                // 创建查询对象
                SimpleQuery simpleQuery = new SimpleQuery("*:*");

                // 设置分页起始记录数
                simpleQuery.setOffset((page - 1) * rows);
                // 设置页大小
                simpleQuery.setRows(rows);

                // 分页查询
                ScoredPage<SolrItem> scoredPage = solrTemplate.queryForPage(simpleQuery, SolrItem.class);
                // 获取分页数据
                List<SolrItem> solrItemList = scoredPage.getContent();

                // 设置分页数据
                data.put("rows", solrItemList);
                // 设置总页数
                data.put("totalPages", scoredPage.getTotalPages());
                // 设置总记录数
                data.put("total", scoredPage.getTotalElements());
            }

            return data;

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
