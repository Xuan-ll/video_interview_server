package io.openvidu.basic.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("io.openvidu.basic.mapper")	
@ComponentScan("io.openvidu.basic.controller")
public class BasicJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicJavaApplication.class, args);
	}

}
