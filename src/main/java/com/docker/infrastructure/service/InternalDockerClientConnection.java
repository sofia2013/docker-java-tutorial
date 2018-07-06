package com.docker.infrastructure.service;

import com.docker.service.DockerClientConnection;
import com.docker.service.DockerContainerOperations;
import com.docker.service.DockerImageOperations;
import com.docker.service.DockerNetworkOperations;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * @author sofia
 */
public class InternalDockerClientConnection implements DockerClientConnection {

    @Value("${docker.host:}")
    private String dockerHost;

    @Value("${docker.config:}")
    private String dockerConfig;

    @Value("${docker.certPath:}")
    private String dockerCertPath;

    @Value("${docker.tlsVerify:0}")
    private Boolean dockerTlsVerify;

    @Value("${api.version:1.23}")
    private String apiVersion;

    @Value("${registry.username:}")
    private String registryUsername;

    @Value("${registry.password:}")
    private String registryPassword;

    @Value("${registry.email:}")
    private String registryEmail;

    @Value("${registry.url:}")
    private String registryUrl;

    /**
     * 推送镜像延迟时间（单位：秒）
     */
    @Value("${dockerClient.pushImagesAwaitCompletionSeconds:30}")
    private int pushImagesAwaitSeconds;

    /**
     * 获取镜像延迟时间（单位：秒）
     */
    @Value("${dockerClient.pullImagesAwaitCompletionSeconds:30}")
    private int pullImagesAwaitSeconds;

    /**
     * 镜像仓库地址，用于镜像的推送存储，
     * 如 : docker login xxx.xxx.xxx.xxx:xxxx或者放置域名
     */
    @Value("${registryRepository:localhost:9005}")
    private String registryRepository;

    /**
     * 容器启动间隔时间
     */
    @Value("${dockerContainer.intervalSecond:15}")
    private int intervalSecond = 15;

    private DockerContainerOperations dockerContainerOperations;

    private DockerImageOperations dockerImageOperations;

    private DockerNetworkOperations dockerNetworkOperations;

    @PostConstruct
    public void init() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .withDockerTlsVerify(dockerTlsVerify)
                .withDockerCertPath(dockerCertPath)
                .withDockerConfig(dockerConfig)
                .withApiVersion(apiVersion)
                .withRegistryUrl(registryUrl)
                .withRegistryUsername(registryUsername)
                .withRegistryPassword(registryPassword)
                .withRegistryEmail(registryEmail)
                .build();
        DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
        dockerImageOperations = new InternalDockerImageOperations(dockerClient).
                setPullImagesAwaitSeconds(pullImagesAwaitSeconds).
                setPushImagesAwaitSeconds(pushImagesAwaitSeconds).
                setRegistryRepository(registryRepository);
        dockerNetworkOperations = new InternalDockerNetworkOperations(dockerClient);
        dockerContainerOperations = new InternalDockerContainerOperations(dockerClient, dockerNetworkOperations, dockerImageOperations).
                setIntervalSecond(intervalSecond);
    }

    @Override
    public DockerContainerOperations getDockerContainerOperations() {
        return dockerContainerOperations;
    }

    @Override
    public DockerImageOperations getDockerImageOperations() {
        return dockerImageOperations;
    }

    @Override
    public DockerNetworkOperations getDockerNetworkOperations() {
        return dockerNetworkOperations;
    }
}
