package com.yang.server.service;

import com.yang.server.dao.DiskDao;
import com.yang.thrift.disk.DiskData;
import com.yang.thrift.disk.DiskDataService;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DiskDataServiceImpl implements DiskDataService.Iface {

    @Autowired
    public DiskDao diskDao;

    @Override
    public DiskData getCurrentDiskData() throws TException {
        return null;
    }

    @Override
    public List<DiskData> getDiskDataByMin() throws TException {
        int beginTime = 1588152660; // 这里以后换成当前时间（精确到秒）
        List<Integer> timeList = new ArrayList<>();
        for (int i = 1; i <= 60; i++) {
            timeList.add(beginTime - i * 60);
        }
        return diskDao.getDiskDataByMinute(timeList);
    }

    @Override
    public List<DiskData> getDiskDataByHour() throws TException {
        return null;
    }

    @Override
    public List<DiskData> getDiskDataByDay() throws TException {
        return null;
    }

    @Override
    public DiskData getDiskDataByTime(int i) throws TException {
        return null;
    }

    @Override
    public List<DiskData> getDiskDataByMinWithTime(int i) throws TException {
        return null;
    }

    @Override
    public List<DiskData> getDiskDataByHourWithTime(int i) throws TException {
        return null;
    }

    @Override
    public List<DiskData> getDiskDataByDayWithTime(int i) throws TException {
        return null;
    }
}
