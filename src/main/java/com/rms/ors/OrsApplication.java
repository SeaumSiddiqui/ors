package com.rms.ors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "applicationAuditAware")
public class OrsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrsApplication.class, args);
	}
		// TODO -> create an admin account on startup
}
