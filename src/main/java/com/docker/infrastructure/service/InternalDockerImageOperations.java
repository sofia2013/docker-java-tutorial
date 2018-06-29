package com.docker.infrastructure.service;

import com.docker.service.DockerImageOperations;
import org.springframework.stereotype.Service;

@Service("dockerClient.dockerImageOperations")
public class InternalDockerImageOperations implements DockerImageOperations {

}
