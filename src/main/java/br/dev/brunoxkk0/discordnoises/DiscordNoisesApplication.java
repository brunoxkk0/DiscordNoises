package br.dev.brunoxkk0.discordnoises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class DiscordNoisesApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscordNoisesApplication.class, args);
    }

}
