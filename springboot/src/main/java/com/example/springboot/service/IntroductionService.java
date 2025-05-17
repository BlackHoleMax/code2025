package com.example.springboot.service;

import cn.hutool.core.date.DateUtil;
import com.example.springboot.entity.Introduction;
import com.example.springboot.mapper.IntroductionMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntroductionService {
    @Resource
    IntroductionMapper introductionMapper;

    public void add(Introduction introduction) {
        introduction.setTime(DateUtil.now());
        introductionMapper.insert(introduction);
    }

    public void update(Introduction introduction) {
        introductionMapper.updateById(introduction);
    }

    public void deleteById(Integer id) {
        introductionMapper.deleteById(id);
    }

    public void deleteBatch(List<Introduction> introductionList) {
        for (Introduction introduction : introductionList) {
            this.deleteById(introduction.getId());
        }
    }

    public List<Introduction> selectAll(Introduction introduction) {
        return introductionMapper.selectAll(introduction);
    }

    public PageInfo<Introduction> selectPage(Integer pageNum, Integer pageSize, Introduction introduction) {
        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Introduction> introductionList = introductionMapper.selectAll(introduction);
        return PageInfo.of(introductionList);
    }
}
