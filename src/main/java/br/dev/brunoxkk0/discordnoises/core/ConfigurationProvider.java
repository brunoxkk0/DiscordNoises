package br.dev.brunoxkk0.discordnoises.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("noise-bot")
public class ConfigurationProvider {
    private String botToken;
    private String basePath = ".";
}
