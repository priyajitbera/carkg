package com.github.priyajitbera.carkg.service.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(
    basePackages = {
      "com.github.priyajitbera.carkg.service.common",
      "com.github.priyajitbera.carkg.service.mcp"
    })
@SpringBootApplication
public class CarKgMcpMainApp {

  public static void main(String[] args) {
    SpringApplication.run(CarKgMcpMainApp.class, args);
  }
}
