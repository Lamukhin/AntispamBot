package com.lamukhin.AntispamBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan
@EnableTransactionManagement
public class AntispamBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(AntispamBotApplication.class, args);
	}

}
