package com.example.qukuailian.dao;

import com.example.qukuailian.bean.Auction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuctionMapper {
    int deleteByPrimaryKey(String auctionId);

    int insert(Auction record);

    int insertSelective(Auction record);

    Auction selectByPrimaryKey(String auctionId);

    int updateByPrimaryKeySelective(Auction record);

    int updateByPrimaryKey(Auction record);
}