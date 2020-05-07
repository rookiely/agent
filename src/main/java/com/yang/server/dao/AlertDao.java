package com.yang.server.dao;

import com.yang.thrift.alert.ThresholdData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AlertDao {

    @Update("UPDATE thresholdtable SET " +
            "`cpuThreshold`=#{cpuThreshold},`memThreshold`=#{memThreshold},`diskThreshold`=#{diskThreshold}")
    boolean updateThreshold(ThresholdData thresholdData);

    @Select("SELECT * FROM thresholdtable")
    List<ThresholdData> getThreshold();
}
