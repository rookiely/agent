package com.yang.agent.thrift;

import com.yang.thrift.server.ServerService;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.transport.TTransport;


public class ProcessorFactoryImpl extends TProcessorFactory {

	public ProcessorFactoryImpl(TProcessor processor) {
		super(processor);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TProcessor getProcessor(TTransport trans) {
        return new ServerService.Processor(new ServerServiceHandler(trans));
	}
}