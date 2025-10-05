package by.victory.ssewsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SsewsappApplication {
    public static void main(String[] args) {
        SpringApplication.run(SsewsappApplication.class, args);
    }
}
