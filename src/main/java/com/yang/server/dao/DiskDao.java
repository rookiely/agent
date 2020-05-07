package com.yang.server.dao;

import com.yang.thrift.disk.DiskData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface DiskDao {

    @Select("SELECT * FROM diskdatatable WHERE time = #{time}")
    List<DiskData> getCurrentDiskData(@Param("time") int time);

    @Select({
            "<script>" +
                    "SELECT * FROM diskdatatable WHERE time in " +
                    "<foreach collection='timeList' item='item' index='index' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>" +
                    "</script>"
    })
    List<DiskData> getDiskDataByMinute(@Param("timeList") List<Integer> timeList); //60分钟内数据

    @Select({
            "<script>" +
                    "SELECT * FROM diskdatatable WHERE time in " +
                    "<foreach collection='timeList' item='item' index='index' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>" +
                    "</script>"
    })
    List<DiskData> getDiskDataByHour(@Param("timeList") List<Integer> timeList); //24小时内数据

    @Select({
            "<script>" +
                    "SELECT * FROM diskdatatable WHERE time in " +
                    "<foreach collection='timeList' item='item' index='index' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>" +
                    "</script>"
    })
    List<DiskData> getDiskDataByDay(@Param("timeList") List<Integer> timeList); //30天内数据

    @Insert("INSERT INTO diskdatatable (`diskName`,`time`,`diskTotal`,`diskFree`,`diskUsed`,`diskAvail`,`diskUsedRatio`) " +
            "VALUES (#{diskName},#{time},#{diskTotal},#{diskFree},#{diskUsed},#{diskAvail},#{diskUsedRatio})")
    boolean insertDiskData(DiskData diskData);
}
