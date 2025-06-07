package com.example.springboot.service;

import cn.hutool.core.util.StrUtil;
import com.example.springboot.entity.Account;
import com.example.springboot.entity.Admin;
import com.example.springboot.exception.CustomerException;
import com.example.springboot.mapper.AdminMapper;
import com.example.springboot.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Resource
    AdminMapper adminMapper;

    public List<Admin> selectAll(Admin admin) {
        return adminMapper.selectAll(admin);
    }

    public void add(Admin admin) {
        Admin dbAdmin = adminMapper.selectByUsername(admin.getUsername());
        if (dbAdmin != null) {
            throw new CustomerException("账号重复了");
        }
        if (!StrUtil.isNotBlank(admin.getPassword())) {
            admin.setPassword("admin");
        }
        adminMapper.insert(admin);
    }

    public void update(Admin admin) {
        adminMapper.updateById(admin);
    }

    public PageInfo<Admin> selectPage(Integer pageNum, Integer pageSize, Admin admin) {
        Account currentUser = TokenUtils.getCurrentUser();
        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> adminList = adminMapper.selectAll(admin);
        return PageInfo.of(adminList);
    }

    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }

    public void deleteBatch(List<Admin> adminList) {
        for (Admin admin : adminList) {
            this.deleteById(admin.getId());
        }
    }

    public Admin selectById(String userId) {
        return adminMapper.selectById(userId);
    }

    public Admin login(Account account) {
        // 验证账号是否存在
        Admin dbAdmin = adminMapper.selectByUsername(account.getUsername());
        if (dbAdmin == null) {
            throw new CustomerException("账号不存在");
        }
        // 验证密码是否正确
        if (!dbAdmin.getPassword().equals(account.getPassword())) {
            throw new CustomerException("账号或密码错误");
        }
        // 创建token并返回给前端
        String token = TokenUtils.createToken(dbAdmin.getId() + "-" + "ADMIN", dbAdmin.getPassword());
        dbAdmin.setToken(token);
        return dbAdmin;
    }

    public void updatePassword(Account account) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser != null) {
            if (!account.getPassword().equals(currentUser.getPassword())) {
                throw new CustomerException("500", "原密码输入错误");
            }
            Admin dbAdmin = adminMapper.selectById(currentUser.getId().toString());
            dbAdmin.setPassword(account.getNewPassword());
            adminMapper.updateById(dbAdmin);
        }
    }
}
