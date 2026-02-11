package pl.smyk.cinemaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableEurekaServer
@EnableKafka
public class CinemaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaServiceApplication.class, args);
    }

}
