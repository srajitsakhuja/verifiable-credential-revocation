package org.tum.thesis.membershipcheckingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.*;

@SpringBootApplication
@ComponentScan("org.tum.thesis.utils.ipfs")
@ComponentScan("org.tum.thesis.server.web.controller")
public class RestServiceApplication {
  public static void main(String[] args) throws IOException {
    SpringApplication.run(RestServiceApplication.class, args);
  }
}
