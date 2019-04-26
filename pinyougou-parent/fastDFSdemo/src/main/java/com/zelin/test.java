package com.zelin;

import org.csource.fastdfs.*;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/26 10:49
 */
public class test {
    public static void main(String[] args) throws Exception {
        //1.初始化fastDFS的上传路径
        ClientGlobal.init("D:\\java1301_2019_114\\pinyougou-parent\\fastDFSdemo\\src\\main\\resources\\fdfs_client.conf");
        //2.构造一个TracerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //3.根据trackerClient得到TrackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //4.定义StorageServer对象
        StorageServer storageServer = null;
        //5.构造StorageClient对象，进行文件上传
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        //6.构造要上传的文件对象
        String[] strings = storageClient.upload_file("E:\\pics\\bk.jpg", "jpg", null);
        //7.打印返回的数组
        for (String string : strings) {
            System.out.println(string);
        }

    }
}
