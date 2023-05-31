package com.metanet.metabus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class MetabusApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetabusApplication.class, args);
	}

}
