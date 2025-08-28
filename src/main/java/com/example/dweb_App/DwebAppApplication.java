package com.example.dweb_App;

import com.google.api.client.util.Value;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Properties;

@SpringBootApplication
public class DwebAppApplication {


	public static void main(String[] args) {

		SpringApplication.run(DwebAppApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CommandLineRunner testConnection(DataSource dataSource) {
		return args -> {
			try (Connection conn = dataSource.getConnection()) {
				System.out.println("✅ SUCCESSFULLY CONNECTED TO DATABASE!");
				System.out.println("URL: " + conn.getMetaData().getURL());
				System.out.println("Username: " + conn.getMetaData().getUserName());
			} catch (Exception e) {

				System.out.println("❌ CONNECTION FAILED: " + e.getMessage());
				e.printStackTrace();
			}
		};
	}
}
