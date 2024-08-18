package com.example.jdkproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class JdkTestApplication {
	public static void main(String[] args) {
		SpringApplication.run(JdkTestApplication.class, args);
	}
}
