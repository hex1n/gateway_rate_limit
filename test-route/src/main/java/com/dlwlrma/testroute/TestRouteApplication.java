package com.dlwlrma.testroute;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.dlwlrma.testroute.mapper.*")
public class TestRouteApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestRouteApplication.class, args);
    }

}
