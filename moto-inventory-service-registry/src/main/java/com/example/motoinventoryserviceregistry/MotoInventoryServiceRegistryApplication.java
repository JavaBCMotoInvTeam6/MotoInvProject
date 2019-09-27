package com.example.motoinventoryserviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MotoInventoryServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MotoInventoryServiceRegistryApplication.class, args);
	}

}
