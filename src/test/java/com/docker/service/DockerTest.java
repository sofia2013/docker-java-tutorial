package com.docker.service;

import com.docker.BaseTestCase;
import com.docker.infrastructure.command.DockerCreateContainerCmd;
import com.github.dockerjava.api.model.AuthConfig;
import org.junit.Test;

import javax.annotation.Resource;

public class DockerTest extends BaseTestCase {
    //从开发者服务器仓库拉取镜像
    //启动容器

    //119.3.26.251 模拟开发者服务器
    //119.3.48.51  模拟生产环境服务器

    @Resource(name = "docker.dockerImageOperations")
    private DockerImageOperations dockerImageOperations;

    @Resource(name = "docker.dockerContainerOperations")
    private DockerContainerOperations dockerContainerOperations;

    @Test
    public void 开发者服务器拉取镜像并启动容器_仓库部署在不同的机器上() {
        String remoteRegistryName = "admin";
        String remoteRegistryPwd = "Skynj123321";
        String remoteRegistryAddress = "http://119.3.26.251:8081/v2/";
        String remoteImageRepository = "192.168.1.108:8081/isv/hello-world:v1.0";

        AuthConfig privateRegistryAuthConfig = new AuthConfig()
                .withUsername(remoteRegistryName)
                .withPassword(remoteRegistryPwd)
                .withRegistryAddress(remoteRegistryAddress);
        dockerImageOperations.pullImageFrom(remoteImageRepository, privateRegistryAuthConfig);
        DockerCreateContainerCommand command = new DockerCreateContainerCmd(remoteImageRepository);
        dockerContainerOperations.createContainer(command);
    }

}
