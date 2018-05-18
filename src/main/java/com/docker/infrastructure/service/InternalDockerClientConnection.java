package com.docker.infrastructure.service;

import com.docker.service.DockerClientConnection;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sofia
 */
@Service("dockerClient.dockerClientConnection")
public class InternalDockerClientConnection implements DockerClientConnection {

    @Autowired
    private DockerClient dockerClient;

    public InternalDockerClientConnection() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        dockerClient = DockerClientBuilder.getInstance(config).build();
    }

    @Override
    public DockerClient connect() {
        return this.dockerClient;
    }
}
