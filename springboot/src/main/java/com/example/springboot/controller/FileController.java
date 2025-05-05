package com.example.springboot.controller;

import cn.hutool.core.io.FileUtil;
import com.example.springboot.common.Result;
import com.example.springboot.exception.CustomerException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 处理文件上传下载相关接口
 */
@RestController
@RequestMapping("/files")
public class FileController {

    /**
     * 文件上传接口
     * 上传路径: http://localhost:8080/files/upload
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception {
        // 找到文件的位置
        String filePath = System.getProperty("user.dir") + "/files/";
        if (FileUtil.isDirectory(filePath)) {
            FileUtil.mkdir(filePath);
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        // 写入文件
        byte[] bytes = file.getBytes();
        FileUtil.writeBytes(bytes, filePath + fileName);
        String url = "http://localhost:8080/files/download/" + fileName;
        return Result.success(url);
    }

    /**
     * 文件下载接口
     * 下载路径: http://localhost:8080/files/download/1.png
     */
    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws Exception {
        // 找到文件的位置
        String filePath = System.getProperty("user.dir") + "/files/";// 当前项目路径
        String realPath = filePath + fileName;
        boolean exist = FileUtil.exist(realPath);
        if (!exist) {
            throw new CustomerException("文件不存在");
        }
        // 读取文件字节流
        byte[] bytes = FileUtil.readBytes(realPath);
        ServletOutputStream os = response.getOutputStream();
        os.write(bytes);
        os.flush();
        os.close();
    }
}
