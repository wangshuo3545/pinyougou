package com.pinyougou.item.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * 消息监听器(生成静态页面)
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-27<p>
 */
public class ItemMessageListener implements SessionAwareMessageListener<TextMessage> {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Reference(timeout = 10000)
    private GoodsService goodsService;
    /** 生成静态页面存储的路径 */
    @Value("${pageDir}")
    private String pageDir;

    @Override
    public void onMessage(TextMessage textMessage, Session session) throws JMSException {
        try{
            System.out.println("=======ItemMessageListener=======");
            // 1. 获取消息的内容
            String goodsId = textMessage.getText();
            System.out.println("goodsId: " + goodsId);

            // 2.获取item.ftl模板文件对应的模板对象
            Template template = freeMarkerConfigurer
                    .getConfiguration().getTemplate("item.ftl");

            // 3. 定义数据模型
            // 根据SPU商品的id 查询商品数据
            Map<String,Object> dataModel = goodsService.getGoods(Long.valueOf(goodsId));

            // 4. 填充模板输出静态的html页面(149187842868021.html)
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(pageDir + goodsId + ".html"), "UTF-8");
            template.process(dataModel, writer);
            writer.flush();
            writer.close();

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
