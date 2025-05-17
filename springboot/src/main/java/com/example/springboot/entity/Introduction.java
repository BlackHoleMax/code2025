package com.example.springboot.entity;

import lombok.Data;

/**
 * 旅游攻略信息
 */
@Data
public class Introduction {
    private Integer id;
    private String img;
    private String title;
    private String content;
    private String time;
}
