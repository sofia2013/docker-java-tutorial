package com.docker.service;

import com.docker.exception.DockerImageException;

public interface DockerImageOperations {

    /**
     * @param dockerFilePath
     * @param imageRepository
     * @param tag
     * @return
     */
    String buildImage(String dockerFilePath, String imageRepository, String tag) throws DockerImageException;

    void pushImage(String imageRepository, String tag) throws DockerImageException;
}
