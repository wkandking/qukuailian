package com.example.qukuailian.dao;

import com.example.qukuailian.bean.User;
import com.example.qukuailian.bean.UserWithBLOBs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(String userid);

    int insert(UserWithBLOBs record);

    int insertSelective(UserWithBLOBs record);

    UserWithBLOBs selectByUserName(String username);

    UserWithBLOBs selectByPrimaryKey(String userid);

    @Select({"select cert from alluser where username = #{username}"})
    byte[] getCertByUserName(String username);

    @Select({"select sig_cert from alluser where username = #{username}"})
    byte[] getSigCertByUserName(String username);

    int updateByPrimaryKeySelective(UserWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(UserWithBLOBs record);

    int updateByPrimaryKey(User record);
}