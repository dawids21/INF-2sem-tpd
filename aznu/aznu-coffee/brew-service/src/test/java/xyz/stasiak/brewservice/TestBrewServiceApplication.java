package xyz.stasiak.brewservice;

import org.springframework.boot.SpringApplication;

public class TestBrewServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(BrewServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
