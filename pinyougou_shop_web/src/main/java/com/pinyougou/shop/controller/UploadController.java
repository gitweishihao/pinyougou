package com.pinyougou.shop.controller;

import com.pinyougou.utils.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    //注入文件服务器地址(ngix服务器，作用加载所有静态资源)
    //${}:sprin El表达式
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        //MultipartFile:springMVC内置对象，用于请求上传文件
        
        //1.获取上传文件的扩展名
        String originalFilename = file.getOriginalFilename();
        //从文件名"."后的第一个字符开始截取
        String extName = originalFilename.
                substring(originalFilename.lastIndexOf(".")+1);

        try {
            //2.创建FastDFS客户端对象(即工具类)
            FastDFSClient fastDFSClient =
                    new FastDFSClient("classpath:config/fdfs_client.conf");
            //3.执行文件上传
            String uploadPath = fastDFSClient.uploadFile(file.getBytes(), extName);
            //4.拼接文件上传服务器返回的url与文件服务器ip地址，拼成完整的url路径
            String url = FILE_SERVER_URL + uploadPath;

            return new Result(true,url);

        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"上传失败！");
        }
    }
}
