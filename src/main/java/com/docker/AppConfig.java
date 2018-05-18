package com.docker;

import com.docker.infrastructure.service.InternalDockerClientConnection;
import com.github.dockerjava.api.DockerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public DockerClient dockerClient() {
        return new InternalDockerClientConnection().connect();
    }
}
