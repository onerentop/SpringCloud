package org.vista.ecms.ribbon.consumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;



/*
 * 开启服务负载均衡
 * 
 * @EnableDiscoveryClient向服务注册中心注册
 * 并且向程序的ioc注入一个bean: restTemplate
 * 并通过@LoadBalanced注解表明这个restRemplate开启负载均衡的功能。
 */
//通过 @EnableHystrix 开启 Hystrix 断路器监控，这个是必须的，并且需要在程序中声明断路点@HystrixCommand
//加上@EnableHystrixDashboard注解，开启HystrixDashboard
@EnableHystrixDashboard
@EnableHystrix
@EnableDiscoveryClient
@SpringBootApplication
public class RibbonConsumerApplication_2 {

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    
	public static void main(String[] args) {
		
		SpringApplication.run(RibbonConsumerApplication_2.class, args);
		//将存放该项目注册信息的接口url写入本地文件
	}
	
}
