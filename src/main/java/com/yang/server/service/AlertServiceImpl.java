package com.yang.server.service;

import com.yang.server.dao.AlertDao;
import com.yang.thrift.alert.AlertService;
import com.yang.thrift.alert.ThresholdData;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AlertServiceImpl implements AlertService.Iface {

    @Autowired
    private AlertDao alertDao;

    @Override
    public boolean updateThreshold(ThresholdData thresholdData) throws TException {
        return alertDao.updateThreshold(thresholdData);
    }
}
