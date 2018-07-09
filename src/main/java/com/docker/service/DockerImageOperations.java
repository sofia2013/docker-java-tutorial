package com.docker.service;

import com.docker.exception.DockerImageException;
import com.docker.model.ImageRepository;
import com.docker.model.RegistryAuthConfig;

public interface DockerImageOperations {

    void pullImageFrom(ImageRepository imageNameWithRepository, RegistryAuthConfig authConfig) throws DockerImageException;

    void pullImage(ImageRepository imageNameWithRepository) throws DockerImageException;

    void pullImage(String imageNameWithRepository) throws DockerImageException;

    String buildImage(String dockerFilePath, ImageRepository imageNameWithRepository) throws DockerImageException;

    void pushImageToLocal(ImageRepository imageNameWithRepository) throws DockerImageException;

    void tagImage(ImageRepository imageNameWithRepository, ImageRepository newImageNameWithRepository) throws DockerImageException;
}
