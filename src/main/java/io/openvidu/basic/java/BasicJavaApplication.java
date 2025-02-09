package io.openvidu.basic.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("io.openvidu.basic.mapper")	
public class BasicJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicJavaApplication.class, args);
	}

}
