package com.yang.server.service;

import com.yang.server.dao.CpuDao;
import com.yang.thrift.cpu.CpuData;
import com.yang.thrift.cpu.CpuDataService;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CpuDataServiceImpl implements CpuDataService.Iface {

    @Autowired
    public CpuDao cpuDao;

    @Override
    public CpuData getCurrentCpuData() throws TException {
        return null;
    }

    @Override
    public List<CpuData> getCpuDataByMin() throws TException {
        int beginTime = 1588152660; // 这里以后换成当前时间（精确到秒）
        List<Integer> timeList = new ArrayList<>();
        for (int i = 1; i <= 60; i++) {
            timeList.add(beginTime - i * 60);
        }
        return cpuDao.getCpuDataByMinute(timeList);
    }

    @Override
    public List<CpuData> getCpuDataByHour() throws TException {
        return null;
    }

    @Override
    public List<CpuData> getCpuDataByDay() throws TException {
        return null;
    }

    @Override
    public CpuData getCpuDataByTime(int i) throws TException {
        return null;
    }

    @Override
    public List<CpuData> getCpuDataByMinWithTime(int i) throws TException {
        return null;
    }

    @Override
    public List<CpuData> getCpuDataByHourWithTime(int i) throws TException {
        return null;
    }

    @Override
    public List<CpuData> getCpuDataByDayWithTime(int i) throws TException {
        return null;
    }
}
