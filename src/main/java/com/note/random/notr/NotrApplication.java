package com.note.random.notr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotrApplication {

	public static void main(String[] args) {
		System.out.println("MYSQL_URL = " + System.getenv("MYSQL_URL"));
		SpringApplication.run(NotrApplication.class, args);
	}

}
