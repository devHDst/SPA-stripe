package com.restapi.paymentcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;

@SpringBootApplication
@EnableTask
public class PaymentcontrolApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentcontrolApplication.class, args);
	}
}
