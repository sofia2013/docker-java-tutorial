package com.docker.service;

import com.docker.BaseTestCase;
import com.docker.model.ImageRepository;
import com.github.dockerjava.api.model.AuthConfig;
import org.junit.Test;

import javax.annotation.Resource;

public class InternalDockerImageOperationsTest extends BaseTestCase {

    @Resource(name = "docker.dockerImageOperations")
    private DockerImageOperations dockerImageOperations;

    @Test
    public void 拉取官方镜像() {
        dockerImageOperations.pullImage("mysql:latest");
    }

    @Test
    public void 拉取指定仓库的镜像() {
        AuthConfig privateRegistryAuthConfig = new AuthConfig()
                .withUsername("admin")
                .withPassword("skynj123321")
                .withRegistryAddress("http://119.3.23.101:8081/v2/");

        ImageRepository imageRepository = new ImageRepository("isv/sky-jack/ecm_mgr");
        imageRepository.setRepository("192.168.0.48:8081");
        imageRepository.setTag("4.1.0");
        dockerImageOperations.pullImageFrom(imageRepository, privateRegistryAuthConfig);
    }

    //region 以下测试可忽略

    @Test
    public void 制作镜像_推送镜像_tomcat8镜像() {
        String tomcat8_filePath = "src/test/resources/imagebuild/tomcat/dockerfile";
        String tomcat8_name = "isv/skytech/tomcat-test";
        String tomcat8_version = "5.0.0";
        ImageRepository imageRepository = new ImageRepository(tomcat8_name);
        imageRepository.setTag(tomcat8_version);

        dockerImageOperations.buildImage(tomcat8_filePath, imageRepository);
        dockerImageOperations.pushImageToLocal(imageRepository);
    }

    @Test
    public void 制作镜像_推送镜像_java8镜像() {
        String java8_filePath = "src/test/resources/imagebuild/springboot/dockerfile";
        String java8_name = "isv/skytech/jar-test";
        String java8_version = "1.0.0";

        ImageRepository imageRepository = new ImageRepository(java8_name);
        imageRepository.setTag(java8_version);
        dockerImageOperations.buildImage(java8_filePath, imageRepository);

        dockerImageOperations.pushImageToLocal(imageRepository);
    }

    @Test
    public void 制作镜像_推送镜像_mysql5_7镜像() {
        String mysql5_7_filePath = "src/test/resources/imagebuild/mysql/dockerfile";
        String mysql5_7_name = "isv/skytech/mysql-test";
        String mysql5_7_version = "1.0.0";

        ImageRepository imageRepository = new ImageRepository(mysql5_7_name);
        imageRepository.setTag(mysql5_7_version);

        dockerImageOperations.buildImage(mysql5_7_filePath, imageRepository);
        dockerImageOperations.pushImageToLocal(imageRepository);
    }

    @Test
    public void 制作镜像_推送镜像镜像() {
        String filePath = "src/test/resources/imagebuild/tomcat/dockerfile";
        String name = "isv/skytech/tomcat-test";
        String version = "1.0.0";
        ImageRepository imageRepository = new ImageRepository(name);
        imageRepository.setTag(version);

        dockerImageOperations.buildImage(filePath, imageRepository);
        dockerImageOperations.pushImageToLocal(imageRepository);
    }

    //endregion
}
