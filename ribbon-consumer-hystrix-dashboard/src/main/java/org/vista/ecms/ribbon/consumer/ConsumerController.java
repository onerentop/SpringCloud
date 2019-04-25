package org.vista.ecms.ribbon.consumer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
/**
 * 消费提供者方法
 * 描述:调用提供者的 `home` 方法
 **/
@RestController
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;

    //创建了熔断器的功能 ,并指定了defaultStores熔断方法
    //@HystrixCommand 声明短路点，表明该方法为hystrix包裹，
    //可以对依赖服务进行隔离、降级、快速失败、快速重试等等hystrix相关功能 
    //fallbackMethod 降级方法
    //commandProperties 普通配置属性，可以配置HystrixCommand对应属性，例如采用线程池还是信号量隔离、熔断器熔断规则等等
    //ignoreExceptions 忽略的异常，默认HystrixBadRequestException不计入失败
    //groupKey() 组名称，默认使用类名称
    //commandKey 命令名称，默认使用方法名    
    @HystrixCommand(fallbackMethod = "defaultStores")
    @GetMapping(value = "/hello")
    public String hello() {
        return restTemplate.getForEntity("http://eureka-provider/", String.class).getBody();
    }
    
    //熔断方法直接返回了一个字符串， "feign + hystrix ,提供者服务挂了"
    public String defaultStores() {
        return "Ribbon + hystrix ,提供者服务挂了";
    }
}
