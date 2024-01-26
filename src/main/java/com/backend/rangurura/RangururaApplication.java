package com.backend.rangurura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
@Component
public class RangururaApplication {

	public static void main(String[] args) {
		SpringApplication.run(RangururaApplication.class, args);
		System.out.println("Rangurura is running ....");
	}

}
