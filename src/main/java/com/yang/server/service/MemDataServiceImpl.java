package com.yang.server.service;

import com.yang.server.dao.MemDao;
import com.yang.thrift.mem.MemData;
import com.yang.thrift.mem.MemDataService;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MemDataServiceImpl implements MemDataService.Iface {

    @Autowired
    public MemDao memDao;


    @Override
    public MemData getCurrentMemData() throws TException {
        return null;
    }

    @Override
    public List<MemData> getMemDataByMin() throws TException {
        int beginTime = 1588152660; // 这里以后换成当前时间（精确到秒）
        List<Integer> timeList = new ArrayList<>();
        for (int i = 1; i <= 60; i++) {
            timeList.add(beginTime - i * 60);
        }
        return memDao.getMemDataByMinute(timeList);
    }

    @Override
    public List<MemData> getMemDataByHour() throws TException {
        return null;
    }

    @Override
    public List<MemData> getMemDataByDay() throws TException {
        return null;
    }

    @Override
    public MemData getMemDataByTime(int i) throws TException {
        return null;
    }

    @Override
    public List<MemData> getMemDataByMinWithTime(int i) throws TException {
        return null;
    }

    @Override
    public List<MemData> getMemDataByHourWithTime(int i) throws TException {
        return null;
    }

    @Override
    public List<MemData> getMemDataByDayWithTime(int i) throws TException {
        return null;
    }
}
