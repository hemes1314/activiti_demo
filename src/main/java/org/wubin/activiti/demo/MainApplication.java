package org.wubin.activiti.demo;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 排除SecurityAutoConfiguration类 //java.lang.ArrayStoreException: sun.reflect.annotation.TypeNotPresentExceptionProxy
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class MainApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}
	
}
