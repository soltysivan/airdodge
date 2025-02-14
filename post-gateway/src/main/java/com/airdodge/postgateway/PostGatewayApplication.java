package com.airdodge.postgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PostGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostGatewayApplication.class, args);
	}

}
