package com.docker.service;

import com.docker.BaseTestCase;
import com.docker.infrastructure.service.InternalDockerImageOperations;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InternalDockerImageOperationsTest extends BaseTestCase {

    @Autowired
    private InternalDockerImageOperations internalDockerImageOperations;

    private String tomcat8_filePath = "E:\\docker-java-tutorials\\src\\test\\resources\\imagebuild\\tomcat\\dockerfile";
    private String tomcat8_name = "isv/skytech/app1";
    private String tomcat8_version = "4.0.0";

    @Test
    public void 制作镜像_推送镜像_tomcat8镜像() {
        internalDockerImageOperations.buildImage(tomcat8_filePath, tomcat8_name, tomcat8_version);
        internalDockerImageOperations.pushImage(tomcat8_name, tomcat8_version);
    }

    private String java8_filePath = "E:\\docker-java-tutorials\\src\\test\\resources\\imagebuild\\springboot\\dockerfile";
    private String java8_name = "isv/skytech/jar-test";
    private String java8_version = "1.0.0";


    @Test
    public void 制作镜像_推送镜像_java8镜像() {
        internalDockerImageOperations.buildImage(java8_filePath, java8_name, java8_version);
        internalDockerImageOperations.pushImage(java8_name, java8_version);
    }

    private String mysql5_7_filePath= "E:\\docker-java-tutorials\\src\\test\\resources\\imagebuild\\mysql\\dockerfile";
    private String mysql5_7_name = "isv/skytech/mysql-test";
    private String mysql5_7_version = "1.0.0";
    @Test
    public void 制作镜像_推送镜像_mysql5_7镜像() {
        internalDockerImageOperations.buildImage(mysql5_7_filePath, mysql5_7_name, mysql5_7_version);
        internalDockerImageOperations.pushImage(mysql5_7_name, mysql5_7_version);
    }
}
