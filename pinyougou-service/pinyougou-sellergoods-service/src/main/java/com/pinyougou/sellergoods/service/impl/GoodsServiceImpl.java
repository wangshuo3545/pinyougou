package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商品服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-16<p>
 */
@Service(interfaceName = "com.pinyougou.service.GoodsService")
@Transactional(readOnly = false)
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SellerMapper sellerMapper;

    // 开启事务
    @Override
    public void save(Goods goods) {
        try{

            // 设置商品审核状态(未审核)
            goods.setAuditStatus("0");
            // 往商品SPU(标准商品表)插入数据
            goodsMapper.insertSelective(goods);

            // 往商品描述表插入数据(没有主键id)
            goods.getGoodsDesc().setGoodsId(goods.getId());
            goodsDescMapper.insertSelective(goods.getGoodsDesc());

            // 判断是否启用规格
            if ("1".equals(goods.getIsEnableSpec())) { // 启用规格


                // 往商品SKU(库存量单位表)插入数据
                for (Item item : goods.getItems()) {
                    // item: {spec : {}, price : 0, num : 9999, status : '0', isDefault : '0' }
                    // 设置SKU商品标题
                    // Apple iPhone XS Max (A2103) 256GB 金色 全网通（移动4G优先版） 双卡双待
                    // SPU标准商品的标题 + 规格选项的名称 "spec":{"网络":"移动4G","机身内存":"16G"}
                    StringBuilder title = new StringBuilder(goods.getGoodsName());
                    // 把spec字符串 转化成 Map集合
                    Map<String, String> specMap = JSON.parseObject(item.getSpec(), Map.class);
                    for (String optionName : specMap.values()) {
                        title.append(" " + optionName);
                    }
                    item.setTitle(title.toString());

                    // 设置item的信息
                    setItemInfo(item, goods);

                    // 往tb_item表插入数据
                    itemMapper.insertSelective(item);
                }
            }else { // 没有启用规格
                // SPU就是SKU 只需要往tb_item表插入一条数据
                //  {spec : {}, price : 0, num : 9999, status : '0', isDefault : '0' }
                /** 创建SKU具体商品对象 */
                Item item = new Item();
                /** 设置SKU商品的标题 */
                item.setTitle(goods.getGoodsName());
                /** 设置SKU商品的价格 */
                item.setPrice(goods.getPrice());
                /** 设置SKU商品库存数据 */
                item.setNum(9999);
                /** 设置SKU商品启用状态 */
                item.setStatus("1");
                /** 设置是否默认*/
                item.setIsDefault("1");
                /** 设置规格选项 */
                item.setSpec("{}");

                // 设置item的信息
                setItemInfo(item, goods);

                // 往tb_item表插入数据
                itemMapper.insertSelective(item);
            }

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    // 提交事务 还是回滚事务 关键看 目标方法是否引发RuntimeException

    /** 设置商品的信息 */
    private void setItemInfo(Item item, Goods goods) {
        // 设置SKU商品图片 tb_goods_desc
        // [{"color":"金色","url":"http://image.pinyougou.com/jd/wKgMg1qtKEOATL9nAAFti6upbx4132.jpg"},
        //  {"color":"深空灰色","url":"http://image.pinyougou.com/jd/wKgMg1qtKHmAFxj7AAFZsBqChgk725.jpg"},
        // {"color":"银色","url":"http://image.pinyougou.com/jd/wKgMg1qtKJyAHQ9sAAFuOBobu-A759.jpg"}]
        // 把json数组字符串转化成 List<Map>
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList != null && imageList.size() > 0) {
            item.setImage(imageList.get(0).get("url").toString());
        }
        // 设置SKU商品三级分类id
        item.setCategoryid(goods.getCategory3Id());
        // 设置SKU商品创建时间
        item.setCreateTime(new Date());
        // 设置SKU商品修改时间
        item.setUpdateTime(item.getCreateTime());
        // 设置SKU商品关联的SPU的id
        item.setGoodsId(goods.getId());
        // 设置SKU商品商家的id
        item.setSellerId(goods.getSellerId());

        // 设置SKU商品三级分类的名称
        ItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
        item.setCategory(itemCat != null ? itemCat.getName() : "");

        // 设置SKU商品品牌的名称
        Brand brand = brandMapper.selectByPrimaryKey(goods.getBrandId());
        item.setBrand(brand != null ? brand.getName() : "");

        // 设置SKU商品商家的店铺名称
        Seller seller = sellerMapper.selectByPrimaryKey(goods.getSellerId());
        item.setSeller(seller != null ? seller.getNickName() : "");
    }


    @Override
    public void update(Goods goods) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Goods findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Goods> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Goods goods, int page, int rows) {
        try{
            // 开启分页
            PageInfo<Map<String,Object>> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    goodsMapper.findAll(goods);
                }
            });

            // 获取分页的数据
            List<Map<String,Object>> data = pageInfo.getList();
            for (Map<String, Object> map : data){
                // 查询一级分类对象
                ItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(map.get("category1Id"));
                // 设置一级分类名称
                map.put("category1Name", itemCat1 != null ? itemCat1.getName() : "");

                // 查询二级分类对象
                ItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(map.get("category2Id"));
                // 设置二级分类名称
                map.put("category2Name", itemCat2 != null ? itemCat2.getName() : "");

                // 查询三级分类对象
                ItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(map.get("category3Id"));
                // 设置三级分类名称
                map.put("category3Name", itemCat3 != null ? itemCat3.getName() : "");
            }
            return new PageResult(pageInfo.getTotal(), data);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
