package com.bilyoner.assignment.couponapi.configuration;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "com.bilyoner.assignment.couponapi.service.http")
@Configuration
public class HttpClientConfig {

}
