package com.yang.server.dao;

import com.yang.thrift.cpu.CpuData;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CpuDao {

    @Select("SELECT * FROM cpudatatable WHERE time = #{time}")
    List<CpuData> getCurrentCpuData(@Param("time") int time);

    @Select({
            "<script>" +
                    "SELECT * FROM cpudatatable WHERE time in " +
                    "<foreach collection='timeList' item='item' index='index' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>" +
                    "</script>"
    })
    List<CpuData> getCpuDataByMinute(@Param("timeList") List<Integer> timeList); //60分钟内数据

    @Select({
            "<script>" +
                    "SELECT * FROM cpudatatable WHERE time in " +
                    "<foreach collection='timeList' item='item' index='index' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>" +
                    "</script>"
    })
    List<CpuData> getCpuDataByHour(@Param("timeList") List<Integer> timeList); //24小时内数据

    @Select({
            "<script>" +
                    "SELECT * FROM cpudatatable WHERE time in " +
                    "<foreach collection='timeList' item='item' index='index' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>" +
                    "</script>"
    })
    List<CpuData> getCpuDataByDay(@Param("timeList") List<Integer> timeList); //30天内数据

    @Insert("INSERT INTO cpudatatable (`cpuId`,`time`,`cpuCombinedRatio`,`cpuIdleRatio`,`cpuWaitRatio`,`cpuNiceRatio`) " +
            "VALUES (#{cpuId},#{time},#{cpuCombinedRatio},#{cpuIdleRatio},#{cpuWaitRatio},#{cpuNiceRatio})")
    boolean insertCpuData(CpuData cpuData);

}
