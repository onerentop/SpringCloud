package org.vista.ecms.ribbon.consumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.assertj.core.util.Strings;
import org.bouncycastle.math.ec.ECCurve.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
/*
 * 开启服务负载均衡
 * 
 * @EnableDiscoveryClient向服务注册中心注册
 * 并且向程序的ioc注入一个bean: restTemplate
 * 并通过@LoadBalanced注解表明这个restRemplate开启负载均衡的功能。
 */
//通过 @EnableHystrix 开启 Hystrix 断路器监控
@EnableHystrix
@EnableDiscoveryClient
@SpringBootApplication
public class RibbonConsumerApplication_1 {
	
    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

	public static void main(String[] args) {
		SpringApplication.run(RibbonConsumerApplication_1.class, args);
		writeConfig();
	}
	//读取调用该jar包的项目中配置文件的url,存入指定文件，供ReadLocalProperties项目读取
	public static void writeConfig (){
		Properties prop=new Properties();
		try {
			prop.load(Config.class.getResourceAsStream("/application.yml"));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String url="";
	  
     	url=prop.getProperty("url");
	    
     	System.out.println("url---"+url);
		//读取文件中已有的url
		Properties propp=new Properties();
		String oldUrl="";
		try {
		InputStream ins = new FileInputStream(new File("F:\\config.properties"));
		propp.load(ins);
  	
     	oldUrl=propp.getProperty("url");
  	    System.out.println("oldUrl---"+oldUrl);
		    ins.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();		
		}
		
		//拼接原来的url和新的url
     	 try {   
             // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。   
             // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。   
    		FileOutputStream fos = new FileOutputStream("F:\\config.properties",false);  
            System.out.println("fos---"+fos);
            Properties pro = new Properties();
         
            int ifHas=0;
            if(com.google.common.base.Strings.isNullOrEmpty(oldUrl)) {
            	oldUrl= ",";
            }
            String[] strs=oldUrl.split(",");
            for(int i=0;i<strs.length;i++) {
            	if(url.equals(strs[i])) {
            		ifHas=1;
            	}
            }
            //如果配置文件中未存在该接口
            String newUrl="";
            if(ifHas==0) {
            	// 存储
                newUrl=oldUrl+","+url;//拼接已存在的urls和新的url
            }else {
            	newUrl=oldUrl;
            }
        	pro.setProperty("url", newUrl);
            // 以适合使用 load 方法加载到 Properties 表中的格式，   
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流   
               pro.store(fos, newUrl);  
               fos.close();
         } catch (IOException e) {   
             System.err.println("属性文件更新错误");   
         }   
     	
	}
}


