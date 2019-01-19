package com.pinyougou.manager.controller;

import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-16<p>
 */
@RestController
public class UploadController {

    /** 注入文件服务器的访问地址 */
    @Value("${fileServerUrl}")
    private String fileServerUrl;

    /** 文件上传方法 */
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile multipartFile){
        // {status : 200, url : ''}
        Map<String, Object> data = new HashMap<>();
        data.put("status", 500);
        try{
            // 1. 获取上传的文件数据(字节数组)
            byte[] bytes = multipartFile.getBytes();

            // 2. 获取上传的文件名
            String filename = multipartFile.getOriginalFilename();

            // 3. 上传文件到FastDFS文件服务器
            // 3.1 获取配置文件
            String path = this.getClass().getResource("/fastdfs-client.conf").getPath();
            // 3.2 初始化客户端全局对象
            ClientGlobal.init(path);
            // 3.3 创建存储客户端对象
            StorageClient storageClient = new StorageClient();
            // 3.4 调用upload_file()
            String[] arr = storageClient.upload_file(bytes, FilenameUtils.getExtension(filename), null);
            // arr : ["group1", "M00/00/04/xxxxx.jpg"]
            // 访问地址：http://192.168.12.131/group1/M00/00/04/xxxxx.jpg

            // 4. 拼接字符串得到图片访问的URL
            StringBuilder url = new StringBuilder(fileServerUrl);
            for (String str : arr){
                url.append("/" + str);
            }

            data.put("url", url.toString());
            data.put("status", 200);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return data;
    }
}
