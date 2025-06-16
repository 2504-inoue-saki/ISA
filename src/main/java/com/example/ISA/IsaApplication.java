package com.example.ISA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class IsaApplication {

	public static void main(String[] args) {
		SpringApplication.run(IsaApplication.class, args);
	}

}
