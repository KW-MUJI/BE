package com.muji_backend.kw_muji;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KwMujiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KwMujiApplication.class, args);
    }

}
