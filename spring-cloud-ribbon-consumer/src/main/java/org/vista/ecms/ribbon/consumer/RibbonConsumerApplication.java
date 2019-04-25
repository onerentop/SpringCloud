package org.vista.ecms.ribbon.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
/*
 * 开启服务负载均衡
 * 
 * @EnableDiscoveryClient向服务注册中心注册
 * 并且向程序的ioc注入一个bean: restTemplate
 * 并通过@LoadBalanced注解表明这个restRemplate开启负载均衡的功能。
 */
@EnableDiscoveryClient
@SpringBootApplication
public class RibbonConsumerApplication {

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

	public static void main(String[] args) {
		SpringApplication.run(RibbonConsumerApplication.class, args);
	}
}
