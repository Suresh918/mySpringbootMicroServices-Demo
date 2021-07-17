package com.example.mirai.services.gds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication()
@ComponentScan(
		basePackages = { "com.example.mirai" },
		excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.example.mirai.libraries.core.component.ApplicationContextHolder")
)
public class GdsServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(GdsServiceApplication.class, args);
	}
}
