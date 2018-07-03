package com.docker.service;

import com.docker.BaseTestCase;
import com.docker.infrastructure.service.InternalDockerImageOperations;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InternalDockerImageOperationsTest extends BaseTestCase {

    @Autowired
    private InternalDockerImageOperations internalDockerImageOperations;

    private String filePath = "E:\\docker-java-tutorials\\src\\test\\resources\\dockerfile";
    private String name = "isv/skytech/app1";
    private String version = "2.0.0";

    @Test
    public void 制作镜像_推送镜像() {
        internalDockerImageOperations.buildImage(filePath, name, version);
        internalDockerImageOperations.pushImage(name, version);
    }
}
