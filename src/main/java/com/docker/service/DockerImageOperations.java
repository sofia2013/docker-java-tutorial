package com.docker.service;

import com.docker.exception.DockerImageException;
import com.github.dockerjava.api.model.AuthConfig;

public interface DockerImageOperations {

    void pullImageFrom(String imageRepository, AuthConfig authConfig) throws DockerImageException;

    void pullImage(String imageRepository) throws DockerImageException;

    /**
     * @param dockerFilePath
     * @param imageRepository
     * @param tag
     * @return
     */
    String buildImage(String dockerFilePath, String imageRepository, String tag) throws DockerImageException;

    void pushImage(String imageRepository, String tag) throws DockerImageException;
}
