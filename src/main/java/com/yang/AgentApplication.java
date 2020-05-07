package com.yang;

import com.yang.server.RPCThriftServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.yang.server.dao")
public class AgentApplication {

	private static RPCThriftServer rpcThriftServer;
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(AgentApplication.class, args);
		try {
			rpcThriftServer = context.getBean(RPCThriftServer.class);
			rpcThriftServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
