package com.jpriva.orders;

import org.springframework.boot.SpringApplication;

public class TestOrdersApplication {

	static void main(String[] args) {
		SpringApplication.from(OrdersApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
