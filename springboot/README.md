# Getting Started

## 项目配置

### 接口实体类配置
```java
@Data
public class Result {
    private String code;
    private Object data;
    private String msg;

    public static Result success() {
        Result result = new Result();
        result.setCode("200");
        result.setMsg("请求成功");
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setCode("200");
        result.setData(data);
        result.setMsg("请求成功");
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.setCode("500");
        result.setMsg(msg);
        return result;
    }

    public static Result error(String code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
```

配置通用实体类Result用于接口返回值，请求体包含三个属性：
- Result
  - code
  - data
  - msg

之后编写四个方法：
1. success方法（只返回状态码和请求成功消息） 
2. success方法（返回状态码、消息，可携带数据)
3. error方法（返回状态码，可自定义消息）
4. error方法（自定义状态码和消息）

### 配置全局错误处理器

配置全局错误处理器，配置返回系统错误：
```java
@ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统异常");
    }
```

配置springboot默认日志处理器(slf4j)
```java
private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
```

## Mybatis集成

### 安装库文件

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```
```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.4</version>
</dependency>
```

### 编写数据库查询字段

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.springboot.mapper.AdminMapper">

    <select id="selectAll" resultType="com.example.springboot.entity.Admin">
        SELECT * FROM `admin` order by id desc
    </select>

    <select id="selectById" parameterType="int" resultType="com.example.springboot.entity.Admin">
        SELECT * FROM admin WHERE id = #{id}
    </select>

    <update id="updateById" parameterType="com.example.springboot.entity.Admin">
        UPDATE admin
        SET username = IF(#{username} IS NOT NULL, #{username}, username),
            password = IF(#{password} IS NOT NULL, #{password}, password)
        WHERE id = #{id}
    </update>

    <delete id="deleteById" parameterType="int">
        DELETE FROM admin WHERE id = #{id}
    </delete>
</mapper>
```

### 配置数据库
```properties
# 配置数据库驱动
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# 数据库账户
spring.datasource.username=root
#数据库密码
spring.datasource.password=your-password
# 数据库url
spring.datasource.url=jdbc:mysql://localhost:3306/code2025?characterEncoding=UTF-8&useUnicode=true&connectionCollation=utf8mb4_unicode_ci&serverTimezone=Asia/Shanghai

# 配置mapper xml文件路径
mybatis.mapper-locations=classpath:mapper/*.xml
# 配置mybatis日志
mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
# 开启mybatis下划线驼峰命名映射
mybatis.configuration.map-underscore-to-camel-case=true
```

### 配置mapper接口

```java
package com.example.springboot.mapper;

import com.example.springboot.entity.Admin;

import java.util.List;

public interface AdminMapper {
    List<Admin> selectAll();

    Admin selectById(int id);
}
```

### 配置service类

**编写方法调用mapper接口，实现数据库查询**

```java
public List<Admin> selectAll() {
    return adminMapper.selectAll();
}
```

### controller层调用mapper

在controller类中使用`@Resource`注解依赖注入service，并在controller接口中调用方法

**@Resource 是Java中的一个注解，它属于JSR-250规范，用于实现依赖注入。依赖注入是一种设计模式，它允许对象在运行时获取其依赖的其他对象，而不是在对象内部创建这些依赖。**

```java
@Resource
AdminService adminService;
```

```java
@GetMapping("/selectAll")
public Result selectAll() {
    List<Admin> adminList = adminService.selectAll();
    return Result.success(adminList);
}
```

## 分页查询

# 引入PageHelper仓库

```xml
<dependency>
  <groupId>com.github.pagehelper</groupId>
  <artifactId>pagehelper-spring-boot-starter</artifactId>
  <version>1.4.6</version>
  <exclusions>
    <exclusion>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

# service类编写查询方法

**开启分页查询传入两个参数，pageNum（页码）和pageSize（每页数据大小）**

```java
public PageInfo<Admin> selectPage(Integer pageNum, Integer pageSize) {
    PageHelper.startPage(pageNum, pageSize); // 开启分页查询
    List<Admin> adminList = adminMapper.selectAll();
    return PageInfo.of(adminList); // 根据传入的从数据库查询到的列表生成PageInfo对象返回
}
```

# 配置controller使用分页查询方法

```java
@GetMapping("/selectPage")
public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                         @RequestParam(defaultValue = "5") Integer pageSize){
    PageInfo<Admin> adminPageInfo = adminService.selectPage(pageNum, pageSize);
    return Result.success(adminPageInfo); // 成功查询接口返回成功，并携带数据
}
```

## 多参数分页

**接口参数传入Admin对象，mybatis可以直接获取**
```java
 @GetMapping("/selectPage")
public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                         @RequestParam(defaultValue = "5") Integer pageSize,
                         Admin admin) {
    PageInfo<Admin> adminPageInfo = adminService.selectPage(pageNum, pageSize, admin);
    return Result.success(adminPageInfo);
}
```

**xml字段编写if标签，通过like关键字和concat拼接完成模糊查询，再用and链接两条where条件语句**
```xml
<select id="selectAll" resultType="com.example.springboot.entity.Admin">
  SELECT * FROM `admin`
  <where>
    <if test="username != null">username like concat('%',#{username},'%')</if>
    <if test="name != null">and name like concat('%',#{name},'%')</if>
  </where>
  order by id desc
</select>
```
前端通过axios直接在params属性中传入需要的查询的字符，此处是username和name属性即可实现模糊查询。
