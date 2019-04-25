package org.vista.ecms.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
//通过 @EnableZuulProxy 开启 Zuul 服务网关
@EnableZuulProxy
@SpringBootApplication
public class ZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulApplication.class, args);
	}
	
	@Bean
	public TokenFilter tokenFilter() {
		return new TokenFilter();
	}

	@Bean
	public PasswordFilter PasswordFilter() {
		return new PasswordFilter();
	}
}
