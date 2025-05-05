package com.example.springboot.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Admin;
import com.example.springboot.service.AdminService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    AdminService adminService;

    @PostMapping("/add")
    public Result add(@RequestBody Admin admin) {  // @RequestBody 接收前端传来的json参数
        adminService.add(admin);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Admin admin) {  // @RequestBody 接收前端传来的json参数
        adminService.update(admin);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteBatch(@PathVariable Integer id) {  // @PathVariable 接收前端传来的路径参数
        adminService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Admin> adminList) {  // @RequestBody 接收前端传来的json参数
        adminService.deleteBatch(adminList);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll(Admin admin) {
        List<Admin> adminList = adminService.selectAll(admin);
        return Result.success(adminList);
    }

    /**
     * 分页查询
     * pageNum: 当前页码
     * pageSize: 每页的个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "5") Integer pageSize,
                             Admin admin) {
        PageInfo<Admin> adminPageInfo = adminService.selectPage(pageNum, pageSize, admin);
        return Result.success(adminPageInfo);
    }

    /**
     * 数据导出
     */
    @GetMapping("/export")
    public void exportData(Admin admin, HttpServletResponse response) throws Exception {
        String ids = admin.getIds();
        if (StrUtil.isNotBlank(ids)) {
            String[] idsArr = ids.split(",");
            admin.setIdsArr(idsArr);
        }
        // 1. 拿到所有数据
        List<Admin> list = adminService.selectAll(admin);
        // 2. 构建writer对象
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 3. 构建中文表头
        writer.addHeaderAlias("username", "账号");
        writer.addHeaderAlias("name", "名称");
        writer.addHeaderAlias("phone", "电话");
        writer.addHeaderAlias("email", "邮箱");
        // 只写出标注别名alias的字段
        writer.setOnlyAlias(true);
        // 4. 写出数据到writer
        writer.write(list);
        // 5. 设置输出的文件的名称以及输出流的头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("管理员信息", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        // 6. 写出到输出流 并关闭writer
        ServletOutputStream sos = response.getOutputStream();
        writer.flush(sos);
        writer.close();
        sos.close();
    }

    /**
     * 批量导入
     *
     * @return Result
     */
    @PostMapping("/import")
    public Result importData(MultipartFile file) throws Exception {
        // 1. 拿到输入流 构建Reader
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 2. 通过Reader 读取excel里的数据
        reader.addHeaderAlias("账号", "username");
        reader.addHeaderAlias("名称", "name");
        reader.addHeaderAlias("电话", "phone");
        reader.addHeaderAlias("邮箱", "email");
        List<Admin> list = reader.readAll(Admin.class);
        // 3. 将数据写入到数据库
        for (Admin admin : list) {
            adminService.add(admin);
        }
        return Result.success();
    }
}
