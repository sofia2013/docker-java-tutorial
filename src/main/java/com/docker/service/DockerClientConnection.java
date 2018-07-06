package com.docker.service;

/**
 * @author sofia
 */
public interface DockerClientConnection {

    /**
     * @return
     */
    DockerContainerOperations getDockerContainerOperations();

    /**
     * @return
     */
    DockerImageOperations getDockerImageOperations();

    /**
     * @return
     */
    DockerNetworkOperations getDockerNetworkOperations();
}
