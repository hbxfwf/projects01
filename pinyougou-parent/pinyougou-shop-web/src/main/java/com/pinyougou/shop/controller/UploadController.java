package com.pinyougou.shop.controller;

import com.pinyougou.pojo.Result;
import com.pinyougou.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/26 11:05
 */
@RestController
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String fileServerUrl;
    /**
     * 进行文件上传
     * @param file
     * @return
     */
    @RequestMapping("/upload")
    public Result uploadFile(MultipartFile file){
        try {
            //1.得到要上传的文件名
            String filename = file.getOriginalFilename();
            //2.取得文件的后缀名
            String suffixName = filename.substring(filename.lastIndexOf(".")+1);
            //3.使用fastDFS进行文件上传
            //3.1)构造工具类进行文件上传
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //4.利用工具类进行文件上传
            String url = fastDFSClient.uploadFile(file.getBytes(), suffixName);
            //5.重新拼凑url地址
            url = fileServerUrl + url;
            //6.返回结果集，可以将当前返回的url作为message返回
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"上传失败！");
        }
    }
}
