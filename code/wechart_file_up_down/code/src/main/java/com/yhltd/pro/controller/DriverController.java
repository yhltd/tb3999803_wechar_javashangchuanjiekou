package com.yhltd.pro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.yhltd.pro.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author hui
 * @date 2022/11/29 18:46
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class DriverController {

    /**
     * 上传
     *
     * @return ResultInfo
     */
    @RequestMapping("/upload")
    public ResultInfo upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException {
        //获取原始名称
        long kongjian = Long.parseLong(request.getParameter("kongjian"));
        String thisPath = request.getParameter("path");
        String fileName = file.getOriginalFilename();
        //文件保存路径

//        本地测试
//        String filePath = "D:/profile" + thisPath;

//        公司服务器路径
        String filePath = "C:/iis_jxc/sharepic_path" + thisPath;

        //文件重命名,防止重复
        filePath = filePath + fileName;
        //文件对象
        File dest = new File(filePath);
        //判断路径是否存在,如果不存在则创建
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }
        try {
//            本地测试
//            Path folder = Paths.get("D:/profile" + thisPath);

//            公司服务器路径
            Path folder = Paths.get("C:/iis_jxc/sharepic_path" + thisPath);
            long size = Files.walk(folder)
                    .filter(path -> path.toFile().isFile())
                    .mapToLong(path -> path.toFile().length())
                    .sum();
            size = size / 1073741824;
            if(size >= kongjian){
                return ResultInfo.success("存储空间不足", filePath);
            }else{
                //保存到服务器中
                file.transferTo(dest);
                return ResultInfo.success("上传成功", filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 上传
     *
     * @return ResultInfo
     */
    @RequestMapping("/uploadBase64")
    public ResultInfo uploadBase64(HttpServletRequest request) throws IOException {
        //获取原始名称
        long kongjian = Long.parseLong(request.getParameter("kongjian"));
        String thisPath = request.getParameter("path");
        String fileName = request.getParameter("fileName");
        String fileType = fileName.split(",")[fileName.split(",").length - 1];
        String file = request.getParameter("file").replaceAll(" ","+");
        file = file.replaceAll("[^A-Za-z0-9+/=]", "");
        String MiMe = "image/" + fileType;
        System.out.println(kongjian);
        System.out.println(thisPath);
        System.out.println(fileName);
        System.out.println(file);

        MultipartFile new_file = convertToMultipartFile(file,fileName,MiMe);

        //文件保存路径
//        本地测试
//        String filePath = "D:/profile" + thisPath;

//        公司服务器路径
        String filePath = "C:/iis_jxc/sharepic_path" + thisPath;

        //文件重命名,防止重复
        filePath = filePath + fileName;
        //文件对象
        File dest = new File(filePath);
        //判断路径是否存在,如果不存在则创建
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }

        try {
//            本地测试
//            Path folder = Paths.get("D:/profile" + thisPath);

//            公司服务器路径
            Path folder = Paths.get("C:/iis_jxc/sharepic_path" + thisPath);

            long size = Files.walk(folder)
                    .filter(path -> path.toFile().isFile())
                    .mapToLong(path -> path.toFile().length())
                    .sum();
            size = size / 1073741824;
            if(size >= kongjian){
                return ResultInfo.success("存储空间不足", filePath);
            }else{
                //保存到服务器中
                new_file.transferTo(dest);
                return ResultInfo.success("上传成功", filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MultipartFile convertToMultipartFile(String base64String, String fileName, String contentType) {
        // 解码Base64字符串
        byte[] decodedBytes = Base64.getDecoder().decode(base64String.getBytes(StandardCharsets.UTF_8));

        // 创建一个MockMultipartFile对象
        MultipartFile multipartFile = new MockMultipartFile(
                "file", // 参数名，通常是"file"
                fileName, // 原始文件名
                contentType, // MIME类型，例如"image/jpeg"
                decodedBytes // 解码后的字节数组
        );

        return multipartFile;
    }


    /**
     * 删除
     *
     * @return ResultInfo
     */
    @RequestMapping("/delete")
    public ResultInfo delete(HttpServletRequest request) throws IOException {
        //获取原始名称
        String orderNumber = request.getParameter("order_number");
        String path = request.getParameter("path");
        for (int i=1; i<=14; i++) {
            String filepath = "";
            if(i < 10){
                filepath = "C:/iis_jxc/sharepic_path" + path + orderNumber + "-0" + i + ".jpg";
            }else{
                filepath = "C:/iis_jxc/sharepic_path" + path + orderNumber + "-" + i + ".jpg";
            }
            File dir = new File(filepath);
            if(dir.isFile() && dir.exists()) {
                dir.delete();
            }
        }
        return ResultInfo.success("删除成功",orderNumber);
    }

    /**
     * 删除
     *
     * @return ResultInfo
     */
    @RequestMapping("/deleteBase64")
    public ResultInfo deleteBase64(HttpServletRequest request) throws IOException {
        //获取原始名称
        String path = request.getParameter("path");
        for (int i=1; i<=14; i++) {
            String filepath = "";
            if(i < 10){
                filepath = "C:/iis_jxc/sharepic_path" + path;
            }else{
                filepath = "C:/iis_jxc/sharepic_path" + path;
            }
            File dir = new File(filepath);
            if(dir.isFile() && dir.exists()) {
                dir.delete();
            }
        }
        return ResultInfo.success("删除成功",path);
    }


}


