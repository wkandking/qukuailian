package com.example.qukuailian.dao;

import com.example.qukuailian.bean.Auction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuctionMapper {
    int deleteByPrimaryKey(Integer flag);

    int insert(Auction record);

    int insertSelective(Auction record);

    Auction selectByPrimaryKey(Integer flag);

    Auction selectByAuctionId(String auctionId);

    int updateByPrimaryKeySelective(Auction record);

    int updateByPrimaryKey(Auction record);
}