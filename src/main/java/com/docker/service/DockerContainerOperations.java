package com.docker.service;

import com.docker.exception.DockerContainerException;
import com.docker.infrastructure.command.DockerCreateContainerCmd;
import com.docker.model.DockerContainer;

import java.util.List;

/**
 * @author sofia
 */
public interface DockerContainerOperations {

    /**
     * 新建容器
     *
     * @param dockerContainer
     * @return
     */
    DockerContainer createContainer(DockerCreateContainerCommand dockerContainer) throws DockerContainerException;

    /**
     * 新建容器
     *
     * @param imageName 镜像名称
     * @return
     */
    DockerContainer createASimpleContainer(String imageName) throws DockerContainerException;

    /**
     * 查看容器信息
     *
     * @param containerId 容器标识
     * @return
     */
    DockerContainer inspectAContainer(String containerId);

    /**
     * 删除容器
     *
     * @param containerId 容器标识
     */
    void removeContainer(String containerId) throws DockerContainerException;

    /**
     * 停止容器
     *
     * @param containerId
     */
    void stopContainer(String containerId) throws DockerContainerException;

    /**
     * 重启容器
     *
     * @param containerId 容器标识
     */
    void restartContainer(String containerId) throws DockerContainerException;

    /**
     * 新建容器栈
     *
     * @param containers
     */
    List<DockerContainer> createContainers(List<DockerCreateContainerCmd> containers) throws DockerContainerException;


    /**
     * @param containers
     * @param netWorkName
     */
    List<DockerContainer> createContainersWithNewNetWork(List<DockerCreateContainerCmd> containers, String netWorkName) throws DockerContainerException;

    /**
     * @param containers
     * @param netWorkId
     */
    List<DockerContainer> createContainersWithNetWorkId(List<DockerCreateContainerCmd> containers, String netWorkId) throws DockerContainerException;

    /**
     * @param containers
     * @param NetWorkName
     */
    List<DockerContainer> createContainersWithNetWorkName(List<DockerCreateContainerCmd> containers, String NetWorkName) throws DockerContainerException;

}
