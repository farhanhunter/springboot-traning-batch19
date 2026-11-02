package com.bootcamp19.registryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer // menjadikan aplikasi ini sebagai Eureka Server (service registry)
public class RegistryserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistryserviceApplication.class, args);
	}

}
