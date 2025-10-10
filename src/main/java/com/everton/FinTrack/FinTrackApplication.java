package com.everton.FinTrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAsync
@SpringBootApplication
public class FinTrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinTrackApplication.class, args);
	}

}
