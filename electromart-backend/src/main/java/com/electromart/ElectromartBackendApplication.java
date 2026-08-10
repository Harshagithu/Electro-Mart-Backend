package com.electromart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ElectromartBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElectromartBackendApplication.class, args);
	}

}
