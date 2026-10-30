package org.kelompokwira.wirakopi.wirakopi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@SpringBootApplication
public class WirakopiApplication {
	public static enum Something{
		Email,
		Password,
	}

	public static void main(String[] args) {
		SpringApplication.run(WirakopiApplication.class, args);
	}

}
