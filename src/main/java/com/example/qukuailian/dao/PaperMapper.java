package com.example.qukuailian.dao;

import com.example.qukuailian.bean.Paper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaperMapper {
    int deleteByPrimaryKey(String paperId);

    int insert(Paper record);

    int insertSelective(Paper record);

    Paper selectByPrimaryKey(String paperId);

    int updateByPrimaryKeySelective(Paper record);

    int updateByPrimaryKey(Paper record);
}