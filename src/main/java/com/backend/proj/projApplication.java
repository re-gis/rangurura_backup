package com.backend.proj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
public class projApplication {

	public static void main(String[] args) {
		SpringApplication.run(projApplication.class, args);
		System.out.println("Rangurura is running ....");
	}

}
