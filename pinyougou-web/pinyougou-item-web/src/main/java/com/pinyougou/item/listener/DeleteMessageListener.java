package com.pinyougou.item.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

/**
 * 消息监听器(删除静态页面)
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-27<p>
 */
public class DeleteMessageListener implements SessionAwareMessageListener<ObjectMessage> {

    /** 生成静态页面存储的路径 */
    @Value("${pageDir}")
    private String pageDir;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        try{
            System.out.println("=======DeleteMessageListener=========");
            // 1. 获取消息内容
            Long[] goodsIds = (Long[])objectMessage.getObject();

            // 2. 删除静态的页面
            for (Long goodsId : goodsIds){
                File file = new File(pageDir + goodsId + ".html");
                if (file.exists() && file.isFile()){
                    // 删除文件
                    file.delete();
                }
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
