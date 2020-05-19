package com.yang.agent.thrift;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AgentUtil {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${thrift.port}")
    private int port;

    public void startAgent(){
        try {
            TServerSocket tServerSocket = new TServerSocket(port);
            TThreadPoolServer.Args targs = new TThreadPoolServer.Args(tServerSocket);
            TBinaryProtocol.Factory factory=new TBinaryProtocol.Factory();
            TProcessorFactory tProcessorFactory = new ProcessorFactoryImpl(null);
            targs.protocolFactory(factory);
            targs.processorFactory(tProcessorFactory);
            TThreadPoolServer tThreadPoolServer = new TThreadPoolServer(targs);
            logger.info("thrift服务启动成功, 端口={}", port);
            tThreadPoolServer.serve();

        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
