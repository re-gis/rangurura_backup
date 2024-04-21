package com.backend.proj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class projApplication {

	public static void main(String[] args) {
		SpringApplication.run(projApplication.class, args);
		System.out.println("Rangurura is running ....");
	}

}
