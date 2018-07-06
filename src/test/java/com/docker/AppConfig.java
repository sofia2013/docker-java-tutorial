package com.docker;

import com.docker.infrastructure.service.InternalDockerClientConnection;
import com.docker.service.DockerClientConnection;
import com.docker.service.DockerContainerOperations;
import com.docker.service.DockerImageOperations;
import com.docker.service.DockerNetworkOperations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    /**
     * docker连接
     *
     * @return
     */
    @Bean(value = "docker.dockerClientConnection")
    public DockerClientConnection dockerClientConnection() {
        return new InternalDockerClientConnection();
    }

    /**
     * docker镜像操作服务
     * @param connection
     * @return
     */
    @Bean(value = "docker.dockerImageOperations")
    public DockerImageOperations dockerImageOperations(@Qualifier("docker.dockerClientConnection") DockerClientConnection connection) {
        return connection.getDockerImageOperations();
    }

    /**
     * docker
     * @param connection
     * @return
     */
    @Bean(value = "docker.dockerNetworkOperations")
    public DockerNetworkOperations dockerNetworkOperations(@Qualifier("docker.dockerClientConnection") DockerClientConnection connection) {
        return connection.getDockerNetworkOperations();
    }

    @Bean(value = "docker.dockerContainerOperations")
    public DockerContainerOperations dockerContainerOperations(@Qualifier("docker.dockerClientConnection") DockerClientConnection connection) {
        return connection.getDockerContainerOperations();
    }


}
