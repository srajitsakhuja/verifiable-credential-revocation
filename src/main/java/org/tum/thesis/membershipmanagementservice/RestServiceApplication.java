package org.tum.thesis.membershipmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.tum.thesis.utils.ipfs")
@ComponentScan("org.tum.thesis.membershipmanagementservice")
public class RestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }
}
