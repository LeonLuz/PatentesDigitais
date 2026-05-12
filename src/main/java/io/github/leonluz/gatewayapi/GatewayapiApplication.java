package io.github.leonluz.gatewayapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class GatewayapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayapiApplication.class, args);
	}

}
