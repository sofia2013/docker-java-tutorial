package com.docker.infrastructure.service;

import com.docker.exception.DockerNetWorkException;
import com.docker.service.DockerNetworkOperations;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Network;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class InternalDockerNetworkOperations implements DockerNetworkOperations {

    private DockerClient dockerClient;

    public InternalDockerNetworkOperations(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public void createNetWork(String name) throws DockerNetWorkException {
        if (StringUtils.isBlank(name)) {
            throw new DockerNetWorkException("createNetWork-001", "子网名称不能为NULL或者空字符串。");
        }

        Boolean exists = this.networkExistsByName(name);
        if (exists) {
            throw new DockerNetWorkException("createNetWork-002", String.format("创建子网 %s 失败，子网 %s 已经存在。", name));
        }

        dockerClient.createNetworkCmd().withName(name).exec();
    }

    @Override
    public Boolean networkExistsByName(String name) {
        List<Network> result = dockerClient.listNetworksCmd().withNameFilter(name).exec();
        return !CollectionUtils.isEmpty(result);
    }

}
