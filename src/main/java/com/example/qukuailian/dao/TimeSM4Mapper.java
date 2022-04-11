package com.example.qukuailian.dao;

import com.example.qukuailian.bean.TimeSM4;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TimeSM4Mapper {
    int deleteByPrimaryKey(String tstring);

    int insert(TimeSM4 record);

    int insertSelective(TimeSM4 record);

    TimeSM4 selectByPrimaryKey(String tstring);

    int updateByPrimaryKeySelective(TimeSM4 record);

    int updateByPrimaryKey(TimeSM4 record);
}