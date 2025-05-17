package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Introduction;
import com.example.springboot.service.IntroductionService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/introduction")
public class IntroductionController {
    @Resource
    IntroductionService introductionService;

    @PostMapping("/add")
    public Result add(@RequestBody Introduction introduction) {  // @RequestBody 接收前端传来的json参数
        introductionService.add(introduction);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Introduction introduction) {  // @RequestBody 接收前端传来的json参数
        introductionService.update(introduction);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteBatch(@PathVariable Integer id) {  // @PathVariable 接收前端传来的路径参数
        introductionService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll(Introduction introduction) {
        List<Introduction> introductionList = introductionService.selectAll(introduction);
        return Result.success(introductionList);
    }

    /**
     * 分页查询
     * pageNum: 当前页码
     * pageSize: 每页的个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "5") Integer pageSize,
                             Introduction introduction) {
        PageInfo<Introduction> introductionPageInfo = introductionService.selectPage(pageNum, pageSize, introduction);
        return Result.success(introductionPageInfo);
    }
}
