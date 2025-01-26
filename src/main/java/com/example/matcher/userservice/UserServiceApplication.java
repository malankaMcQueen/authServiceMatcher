package com.example.matcher.userservice;

import com.example.matcher.userservice.tools.EnvironmentLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableDiscoveryClient
public class UserServiceApplication {
	public static void main(String[] args) {
		System.setProperty("spring.datasource.url", EnvironmentLoader.get("SPRING_DATASOURCE_URL"));
		System.setProperty("spring.datasource.username", EnvironmentLoader.get("SPRING_DATASOURCE_USERNAME"));
		System.setProperty("spring.datasource.password", EnvironmentLoader.get("SPRING_DATASOURCE_PASSWORD"));
		System.setProperty("jwt.secret.access", EnvironmentLoader.get("JWT_SECRET_ACCESS"));
		System.setProperty("jwt.secret.refresh", EnvironmentLoader.get("JWT_SECRET_REFRESH"));
		System.setProperty("spring.security.oauth2.client.registration.google.client-id", EnvironmentLoader.get("GOOGLE_CLIENT_ID"));
		System.setProperty("spring.security.oauth2.client.registration.google.client-secret", EnvironmentLoader.get("GOOGLE_CLIENT_SECRET"));
		System.setProperty("spring.mail.username", EnvironmentLoader.get("SPRING_MAIL_USERNAME"));
		System.setProperty("spring.mail.password", EnvironmentLoader.get("SPRING_MAIL_PASSWORD"));
		System.setProperty("telegram.bot.username", EnvironmentLoader.get("TELEGRAM_BOT_USERNAME"));
		System.setProperty("telegram.bot.token", EnvironmentLoader.get("TELEGRAM_BOT_TOKEN"));
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
