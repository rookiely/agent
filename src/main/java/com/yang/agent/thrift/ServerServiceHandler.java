package com.yang.agent.thrift;

import com.yang.thrift.agent.*;
import com.yang.thrift.server.ScriptFile;
import com.yang.thrift.server.ServerService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransport;
import org.hyperic.sigar.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerServiceHandler implements ServerService.Iface {

    private AgentCallbackService.Client client;
    private boolean isDataAlready = false;

    private List<CpuData> cpuDataList = new ArrayList<>();
    private List<MemData> memDataList = new ArrayList<>();
    private List<DiskData> diskDataList = new ArrayList<>();


    public ServerServiceHandler(TTransport trans) {
        client = new AgentCallbackService.Client(new TBinaryProtocol(trans));
    }

    @Override
    public void getResourceData() throws TException {
        Runnable runnable = () -> {
            int time = getCurrentMinuteTime();
            Sigar sigar = new Sigar();
            String host = getLocalIpAddr();

            cpuDataList.clear();
            memDataList.clear();
            diskDataList.clear();

            CpuPerc[] cpuList = new CpuPerc[0];
            try {
                cpuList = sigar.getCpuPercList();
            } catch (SigarException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < cpuList.length; i++) {
                CpuPerc cpuPerc = cpuList[i];
                CpuData cpuData = new CpuData(i, cpuPerc.getCombined(), cpuPerc.getIdle(), cpuPerc.getWait(),
                        cpuPerc.getNice(), time, host);
                cpuDataList.add(cpuData);
            }

            Mem mem = null;
            try {
                mem = sigar.getMem();
            } catch (SigarException e) {
                e.printStackTrace();
            }
            MemData memData = new MemData(mem.getTotal(), mem.getUsed(), mem.getFree(), mem.getUsedPercent(), time, host);
            memDataList.add(memData);

            FileSystem[] fsList;
            try {
                fsList = sigar.getFileSystemList();
                for (int i = 0; i < fsList.length; i++) {
                    FileSystem fs = fsList[i];
                    FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
                    DiskData diskData = new DiskData(fs.getDevName(), usage.getTotal(), usage.getFree(), usage.getUsed(),
                            usage.getAvail(), usage.getUsePercent(), time, host);
                    diskDataList.add(diskData);
                }
            } catch (SigarException e) {
                e.printStackTrace();
            }
            isDataAlready = true;
        };

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.MINUTES);
        new Thread(new CallbackData()).start();
    }

    @Override
    public void execScript(ScriptFile scriptFile) throws TException {
        try {
            String relativelyPath = System.getProperty("user.dir");
            File file = new File(relativelyPath + File.separator + scriptFile.fileName);
            if (file.exists()) {
                file.delete();
            }

            FileOutputStream fos = new FileOutputStream(file);
            FileChannel channel = fos.getChannel();
            channel.write(scriptFile.fileContent);
            channel.close();
            file.setExecutable(true);

            Process ps;
            if (scriptFile.fileName.contains(".sh")) {
                ps = Runtime.getRuntime().exec("sh " + relativelyPath + File.separator + scriptFile.fileName);
            }else{
                ps = Runtime.getRuntime().exec(relativelyPath + File.separator + scriptFile.fileName);
            }
            ps.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String scriptExecResult = sb.toString();
            String localIpAddr = getLocalIpAddr();
            int time = getCurrentMinuteTime();
            ScriptResultData scriptResultData = new ScriptResultData(0,scriptFile.fileName, localIpAddr, scriptExecResult, time);
            client.pushScriptResult(scriptResultData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class CallbackData implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isDataAlready) {
                    try {
                        client.pushResourceData(cpuDataList, memDataList, diskDataList);
                        isDataAlready = false;
                    } catch (TException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public int getCurrentMinuteTime() {
        long curSec = System.currentTimeMillis();
        curSec -= curSec % (1000 * 60);
        return (int) (curSec / 1000);
    }

    public String getLocalIpAddr() {
        Enumeration<NetworkInterface> nis;
        String ip = null;
        try {
            nis = NetworkInterface.getNetworkInterfaces();
            for (; nis.hasMoreElements(); ) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                for (; ias.hasMoreElements(); ) {
                    InetAddress ia = ias.nextElement();
                    if (ia instanceof Inet4Address && !ia.getHostAddress().equals("127.0.0.1")) {
                        ip = ia.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }
}
