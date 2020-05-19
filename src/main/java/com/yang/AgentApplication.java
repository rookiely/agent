package com.yang;

import com.yang.agent.thrift.AgentUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgentApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(AgentApplication.class, args);
		AgentUtil agent = context.getBean(AgentUtil.class);
		agent.startAgent();
	}

}
