package com.example.springboot.service;

import cn.hutool.core.util.StrUtil;
import com.example.springboot.entity.Account;
import com.example.springboot.entity.User;
import com.example.springboot.exception.CustomerException;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Resource
    UserMapper userMapper;

    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    public void add(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if (dbUser != null) {
            throw new CustomerException("账号重复");
        }
        if (!StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword("123456");
        }
        userMapper.insert(user);
    }

    public void update(User user) {
        userMapper.updateById(user);
    }

    public PageInfo<User> selectPage(Integer pageNum, Integer pageSize, User user) {
        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectAll(user);
        return PageInfo.of(userList);
    }

    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    public void deleteBatch(List<User> userList) {
        for (User user : userList) {
            this.deleteById(user.getId());
        }
    }

    public User selectById(String userId) {
        return userMapper.selectById(userId);
    }

    public User login(Account account) {
        // 验证账号是否存在
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (dbUser == null) {
            throw new CustomerException("账号不存在");
        }
        // 验证密码是否正确
        if (!dbUser.getPassword().equals(account.getPassword())) {
            throw new CustomerException("账号或密码错误");
        }
        // 创建token并返回给前端
        String token = TokenUtils.createToken(dbUser.getId() + "-" + "USER", dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    public void register(User user) {
        this.add(user);
    }
}
