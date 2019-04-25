package org.vista.ecms.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*
 * 开启服务负载均衡
 * 
 * @EnableDiscoveryClient向服务注册中心注册
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ReadApplication {

     
	public static void main(String[] args) {
		
		SpringApplication.run(ReadApplication.class, args);
	}
}
