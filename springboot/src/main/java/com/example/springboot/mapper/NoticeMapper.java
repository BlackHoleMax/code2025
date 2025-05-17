package com.example.springboot.mapper;

import com.example.springboot.entity.Notice;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

public interface NoticeMapper {
    List<Notice> selectAll(Notice notice);

    void insert(Notice notice);

    void updateById(Notice notice);

    @Delete("delete from `notice` where id = #{id}")
    void deleteById(Integer id);
}
