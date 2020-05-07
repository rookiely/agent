package com.yang.server.dao;

import com.yang.thrift.mem.MemData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemDao {

    @Select("SELECT * FROM memdatatable WHERE time = #{time}")
    List<MemData> getCurrentMemData(@Param("time") int time);

    @Select({
            "<script>" +
                    "SELECT * FROM memdatatable WHERE time in " +
                    "<foreach collection='timeList' item='item' index='index' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>" +
                    "</script>"
    })
    List<MemData> getMemDataByMinute(@Param("timeList") List<Integer> timeList); //60分钟内数据

    @Select({
            "<script>" +
                    "SELECT * FROM memdatatable WHERE time in " +
                    "<foreach collection='timeList' item='item' index='index' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>" +
                    "</script>"
    })
    List<MemData> getMemDataByHour(@Param("timeList") List<Integer> timeList); //24小时内数据

    @Select({
            "<script>" +
                    "SELECT * FROM memdatatable WHERE time in " +
                    "<foreach collection='timeList' item='item' index='index' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>" +
                    "</script>"
    })
    List<MemData> getMemDataByDay(@Param("timeList") List<Integer> timeList); //30天内数据

    @Insert("INSERT INTO memdatatable (`time`,`memTotal`,`memUsed`,`memFree`,`memUsedRatio`) " +
            "VALUES (#{time},#{memTotal},#{memUsed},#{memFree},#{memUsedRatio})")
    boolean insertMemData(MemData memData);

}
