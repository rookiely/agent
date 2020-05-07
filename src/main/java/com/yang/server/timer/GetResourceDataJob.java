package com.yang.server.timer;

import com.yang.server.dao.AlertDao;
import com.yang.server.dao.CpuDao;
import com.yang.server.dao.DiskDao;
import com.yang.server.dao.MemDao;
import com.yang.server.service.MailService;
import com.yang.server.util.CurMinUtil;
import com.yang.thrift.alert.ThresholdData;
import com.yang.thrift.cpu.CpuData;
import com.yang.thrift.disk.DiskData;
import com.yang.thrift.mem.MemData;
import org.hyperic.sigar.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Component
public class GetResourceDataJob {

    @Autowired
    private CpuDao cpuDao;
    @Autowired
    private MemDao memDao;
    @Autowired
    private DiskDao diskDao;
    @Autowired
    private AlertDao alertDao;
    @Autowired
    private MailService MailService;

    @Scheduled(cron = "0 */1 * * * ?")
    public void getResourceData() throws SigarException, UnknownHostException {
        int time = CurMinUtil.getCurrentMinuteTime();
        Sigar sigar = new Sigar();

        CpuPerc[] cpuList = sigar.getCpuPercList();
        for (int i = 0; i < cpuList.length; i++) {
            CpuPerc cpuPerc = cpuList[i];
            CpuData cpuData = new CpuData(i, cpuPerc.getCombined(), cpuPerc.getIdle(), cpuPerc.getWait(),
                    cpuPerc.getNice(), time);
            cpuDao.insertCpuData(cpuData);
        }

        Mem mem = sigar.getMem();
        MemData memData = new MemData(mem.getTotal(), mem.getUsed(), mem.getFree(), mem.getUsedPercent(), time);
        memDao.insertMemData(memData);

        FileSystem[] fsList = sigar.getFileSystemList();
        for (int i = 0; i < fsList.length; i++) {
            FileSystem fs = fsList[i];
            FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
            DiskData diskData = new DiskData(fs.getDevName(), usage.getTotal(), usage.getFree(), usage.getUsed(),
                    usage.getAvail(), usage.getUsePercent(), time);
            diskDao.insertDiskData(diskData);
        }

        //发送邮件测试
        boolean cpuFlag = false,memFlag = false,diskFlag = false;
        List<ThresholdData> threshold = alertDao.getThreshold();
        ThresholdData thresholdData = threshold.get(0);
        double cpuThreshold = thresholdData.cpuThreshold;
        double memThreshold = thresholdData.memThreshold;
        double diskThreshold = thresholdData.diskThreshold;

        List<CpuData> currentCpuData = cpuDao.getCurrentCpuData(time);
        for (CpuData cpuDataEle : currentCpuData) {
            if (cpuDataEle.getCpuCombinedRatio() >= cpuThreshold) {
                cpuFlag = true;
                break;
            }
        }

        List<MemData> currentMemData = memDao.getCurrentMemData(time);
        for (MemData memDataEle : currentMemData) {
            double memUsedRatio = memDataEle.getMemUsedRatio() / 100;
            if (memUsedRatio >= memThreshold) {
                memFlag = true;
                break;
            }
        }

        List<DiskData> currentDiskData = diskDao.getCurrentDiskData(time);
        for (DiskData diskDataEle : currentDiskData) {
            if (diskDataEle.getDiskUsedRatio() >= diskThreshold) {
                diskFlag = true;
                break;
            }
        }

        StringBuffer sb = new StringBuffer();
        InetAddress addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress();
        if (cpuFlag) {
            sb.append("CPU利用率超过阈值，");
        }
        if (memFlag) {
            sb.append("内存利用率超过阈值，");
        }
        if (diskFlag) {
            sb.append("磁盘利用率超过阈值，");
        }
        sb.append("\n");

        sb.append("主机").append(ip).append("的资源使用情况如下：\n");
        sb.append("CPU资源使用情况：(CPU利用率阈值为").append(cpuThreshold).append(")\n");
        for (CpuData cpuDataEle : currentCpuData) {
            sb.append("CPU").append(cpuDataEle.getCpuId()).append("的利用率为：").append(cpuDataEle.getCpuCombinedRatio()).append("\n");
        }
        sb.append("\n");

        sb.append("内存资源使用情况：(内存利用率阈值为").append(memThreshold).append(")\n");
        for (MemData memDataEle : currentMemData) {
            double memUsedRatio = memDataEle.getMemUsedRatio() / 100;
            sb.append("内存的利用率为：").append(memUsedRatio).append("\n");
        }
        sb.append("\n");

        sb.append("磁盘资源使用情况：(磁盘利用率阈值为").append(diskThreshold).append(")\n");
        for (DiskData diskDataEle : currentDiskData) {
            sb.append("磁盘").append(diskDataEle.getDiskName()).append("的利用率为：").append(diskDataEle.getDiskUsedRatio()).append("\n");
        }

        if(cpuFlag || memFlag || diskFlag){
            MailService.sendSimpleMail("1243604607@qq.com","资源预警邮件",
                    sb.toString());
        }
    }


}
