package com.bootcamp19.calculatewordservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient // daftar ke Eureka
@EnableFeignClients    // Feign ke API quote pihak ketiga
@EnableKafka           // aktifkan @KafkaListener
@EnableScheduling      // aktifkan @Scheduled (laporan harian)
public class CalculatewordserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalculatewordserviceApplication.class, args);
	}

}
