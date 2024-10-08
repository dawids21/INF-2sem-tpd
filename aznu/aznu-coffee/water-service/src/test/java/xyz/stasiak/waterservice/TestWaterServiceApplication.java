package xyz.stasiak.waterservice;

import org.springframework.boot.SpringApplication;

public class TestWaterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(WaterServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
