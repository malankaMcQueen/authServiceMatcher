package com.example.matcher.userservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
//@EnableDiscoveryClient
public class UserServiceApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load(); // Загружаем переменные из .env
		System.setProperty("spring.datasource.url", dotenv.get("SPRING_DATASOURCE_URL"));
		System.setProperty("spring.datasource.username", dotenv.get("SPRING_DATASOURCE_USERNAME"));
		System.setProperty("spring.datasource.password", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
		System.setProperty("jwt.secret.access", dotenv.get("JWT_SECRET_ACCESS"));
		System.setProperty("jwt.secret.refresh", dotenv.get("JWT_SECRET_REFRESH"));
		System.setProperty("spring.security.oauth2.client.registration.google.client-id", dotenv.get("GOOGLE_CLIENT_ID"));
		System.setProperty("spring.security.oauth2.client.registration.google.client-secret", dotenv.get("GOOGLE_CLIENT_SECRET"));
		System.setProperty("spring.mail.username", dotenv.get("SPRING_MAIL_USERNAME"));
		System.setProperty("spring.mail.password", dotenv.get("SPRING_MAIL_PASSWORD"));
		System.setProperty("telegram.bot.username", dotenv.get("TELEGRAM_BOT_USERNAME"));
		System.setProperty("telegram.bot.token", dotenv.get("TELEGRAM_BOT_TOKEN"));
		System.setProperty("jwt.secret.auth.telegram", dotenv.get("JWT_SECRET_AUTH_TELEGRAM"));
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
