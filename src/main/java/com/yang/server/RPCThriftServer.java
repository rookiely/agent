package com.yang.server;

import com.yang.server.service.*;
import com.yang.thrift.alert.AlertService;
import com.yang.thrift.cpu.CpuDataService;
import com.yang.thrift.disk.DiskDataService;
import com.yang.thrift.mem.MemDataService;
import com.yang.thrift.script.ScriptService;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RPCThriftServer {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${thrift.port}")
    private int port;
    @Value("${thrift.minWorkerThreads}")
    private int minThreads;
    @Value("${thrift.maxWorkerThreads}")
    private int maxThreads;

    private TBinaryProtocol.Factory protocolFactory;
    private TTransportFactory transportFactory;

    @Autowired
    private CpuDataServiceImpl cpuDataService;
    @Autowired
    private MemDataServiceImpl memDataService;
    @Autowired
    private DiskDataServiceImpl diskDataService;
    @Autowired
    private ScriptServiceImpl scriptService;
    @Autowired
    private AlertServiceImpl alertService;

    public void init() {
        protocolFactory = new TBinaryProtocol.Factory();
        transportFactory = new TTransportFactory();
    }

    public void start() {

        try {
            TMultiplexedProcessor processor = new TMultiplexedProcessor();
            processor.registerProcessor("CpuDataService", new CpuDataService.Processor<CpuDataService.Iface>(cpuDataService));
            processor.registerProcessor("MemDataService", new MemDataService.Processor<MemDataService.Iface>(memDataService));
            processor.registerProcessor("DiskDataService", new DiskDataService.Processor<DiskDataService.Iface>(diskDataService));
            processor.registerProcessor("ScriptService", new ScriptService.Processor<ScriptService.Iface>(scriptService));
            processor.registerProcessor("AlertService", new AlertService.Processor<AlertService.Iface>(alertService));

            init();

            TServerTransport transport = new TServerSocket(port);
            TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(transport);
            tArgs.processor(processor);
            tArgs.protocolFactory(protocolFactory);
            tArgs.transportFactory(transportFactory);
            tArgs.minWorkerThreads(minThreads);
            tArgs.maxWorkerThreads(maxThreads);
            TServer server = new TThreadPoolServer(tArgs);
            logger.info("thrift服务启动成功, 端口={}", port);
            server.serve();
        } catch (Exception e) {
            logger.error("thrift服务启动失败", e);
        }

    }
}
