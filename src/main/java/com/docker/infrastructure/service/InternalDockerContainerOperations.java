package com.docker.infrastructure.service;

import com.docker.exception.DockerContainerException;
import com.docker.exception.DockerNetWorkException;
import com.docker.infrastructure.command.DockerCreateContainerCmd;
import com.docker.model.DockerContainer;
import com.docker.model.DockerContainerState;
import com.docker.model.DockerPort;
import com.docker.model.DockerProtocol;
import com.docker.service.DockerContainerBuilder;
import com.docker.service.DockerContainerOperations;
import com.docker.service.DockerCreateContainerCommand;
import com.docker.service.DockerNetworkOperations;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.PullImageResultCallback;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author sofia
 */
@Service("dockerClient.dockerContainerOperations")
public class InternalDockerContainerOperations implements DockerContainerOperations {

    private static final Logger logger = LoggerFactory.getLogger(InternalDockerContainerOperations.class);

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private DockerNetworkOperations dockerNetworkOperations;

    @Value("${dockerContainer.intervalSecond:15}")
    private int intervalSecond = 15;

    /**
     * 新建容器
     *
     * @param command
     * @return
     */
    @Override
    public DockerContainer createContainer(DockerCreateContainerCommand command) throws DockerContainerException {
        this.throwExceptionIfCreateContainerCmdIsNull(command);

        String imageName = command.getImage();

        this.throwExceptionIfImageNameIsNullOrEmpty("createContainer-002", imageName);

        this.pullImage(imageName);
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(imageName);

        String name = command.getName();
        Object[] binds = command.getBinds();
        Integer cpuPeriod = command.getCpuPeriod();
        String cpusetCpus = command.getCpusetCpus();
        String cpusetMems = command.getCpusetMems();
        Integer cpuShares = command.getCpuShares();

        if (StringUtils.isBlank(name) == false) {
            containerCmd.withName(name);
        }
        if (command.getRestartPolicy() != null) {
            containerCmd.withRestartPolicy(RestartPolicy.parse(command.getRestartPolicy().getName() + ":" + command.getRestartPolicy().getMaximumRetryCount()));
        }
        if (binds != null) {
            containerCmd.withBinds((Bind[]) binds);
        }
        if (cpuPeriod != null) {
            containerCmd.withCpuPeriod(cpuPeriod);
        }
        if (StringUtils.isBlank(cpusetCpus) == false) {
            containerCmd.withCpusetCpus(cpusetCpus);
        }
        if (StringUtils.isBlank(cpusetMems) == false) {
            containerCmd.withCpusetMems(cpusetMems);
        }
        if (cpuShares != null) {
            containerCmd.withCpuShares(cpuShares);
        }
        if (command.getLinks() != null) {
            containerCmd.withLinks((Link[]) command.getLinks());
        }
        if (command.getMemory() != null) {
            containerCmd.withMemory(command.getMemory());
        }
        if (command.getMemorySwap() != null) {
            containerCmd.withMemorySwap(command.getMemorySwap());
        }
        if (command.getExposedPorts() != null) {
            containerCmd.withExposedPorts((ExposedPort[]) command.getExposedPorts());
        }
        if (command.getPortBindings() != null) {
            containerCmd.withPortBindings((Ports) command.getPortBindings());
        }
        if (command.getPrivileged() != null) {
            containerCmd.withPrivileged(command.getPrivileged());
        }
        if (command.getVolumes() != null) {
            containerCmd.withVolumes((Volume[]) command.getVolumes());
        }
        if (command.getEnv() != null) {
            containerCmd.withEnv(command.getEnv());
        }
        if (command.getNetworkMode() != null) {
            containerCmd.withNetworkMode(command.getNetworkMode());
        }
        if (command.getMemory() != null) {
            containerCmd.withMemory(command.getMemory());
        }
        if (command.getMemorySwap() != null) {
            containerCmd.withMemorySwap(command.getMemorySwap());
        }

        if (command.getCpusetCpus() != null) {
            containerCmd.withCpusetCpus(command.getCpusetCpus());
        }

        try {
            CreateContainerResponse container = containerCmd.exec();
            dockerClient.startContainerCmd(container.getId()).exec();
            return this.inspectAContainer(container.getId());
        } catch (NotFoundException e) {
            logger.error("新建容器失败", e);
            throw new DockerContainerException("createASimpleContainer-", "新建容器失败。");
        } catch (ConflictException e) {
            logger.error("新建容器失败", e);
            throw new DockerContainerException("createASimpleContainer-", String.format("新建容器失败：容器 %s 已经存在。", containerCmd.getName()));
        } catch (Exception e) {
            logger.error("新建容器失败", e);
            throw new DockerContainerException("createASimpleContainer-", "新建容器失败。");
        }
    }

    /**
     * @param imageName
     * @return
     */
    @Override
    public DockerContainer createASimpleContainer(String imageName) throws DockerContainerException {
        throwExceptionIfImageNameIsNullOrEmpty("createASimpleContainer-001", imageName);

        this.pullImage(imageName);

        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(imageName);
        try {
            CreateContainerResponse container = createContainerCmd.exec();
            dockerClient.startContainerCmd(container.getId()).exec();
            return this.inspectAContainer(container.getId());
        } catch (NotFoundException e) {
            logger.error("新建容器失败。", e);
            throw new DockerContainerException("createASimpleContainer-002", "新建容器失败。");
        }
    }

    /**
     * 下载制定的镜像
     *
     * @param imageName 镜像名称
     */
    private void pullImage(String imageName) {
        ListImagesCmd listImagesCmd = this.dockerClient.listImagesCmd().withImageNameFilter(imageName);
        List<Image> result = listImagesCmd.exec();
        if (CollectionUtils.isEmpty(result) == false) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.info(String.format("镜像 %s 不存在。", imageName));
            logger.info(String.format("开始下载镜像 %s 。", imageName));
        }

        try {
            dockerClient.pullImageCmd(imageName)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (logger.isDebugEnabled()) {
            logger.info(String.format("下载镜像 %s 完毕 。", imageName));
        }
    }

    //region 查看容器

    /**
     * 查看容器
     *
     * @param containerId
     * @return
     */
    @Override
    public DockerContainer inspectAContainer(String containerId) {
        throwExceptionIfContainerIdIsNullOrEmpty("inspectAContainer-001", containerId);

        InspectContainerCmd dockerContainerCmd = dockerClient.inspectContainerCmd(containerId);
        if (dockerContainerCmd == null) {
            return null;
        }

        try {
            InspectContainerResponse containerResponse = dockerContainerCmd.exec();
            DockerContainerBuilder dockerContainerBuilder = new DockerContainerBuilder(containerId);

            dockerContainerBuilder.setName(containerResponse.getName());
            dockerContainerBuilder.setStatus(DockerContainerState.valueOf(StringUtils.upperCase(containerResponse.getState().getStatus())));
            dockerContainerBuilder.setImageId(containerResponse.getImageId());
            dockerContainerBuilder.setCreated(containerResponse.getCreated());
            dockerContainerBuilder.setImageName(this.getImageNameById(containerResponse.getImageId()));

            List<DockerPort> dockerPorts = this.buildExposedPorts(containerResponse);
            if (CollectionUtils.isEmpty(dockerPorts) == false) {
                dockerContainerBuilder.setExposedPorts(dockerPorts);
            }

            Map<DockerPort, List<DockerPort>> portBindings = this.buildPortBindings(containerResponse);

            dockerContainerBuilder.setPortBindings(portBindings);
            return dockerContainerBuilder.build();
        } catch (NotFoundException e) {
            logger.info("查看容器失败。", e);
            return null;
        } catch (Exception e) {
            logger.info("查看容器失败。", e);
            return null;
        }
    }

    //endregion

    //region 停止容器

    /**
     * 停止容器
     *
     * @param containerId
     */
    @Override
    public void stopContainer(String containerId) throws DockerContainerException {
        throwExceptionIfContainerIdIsNullOrEmpty("stopContainer-001", containerId);

        try {
            StopContainerCmd stopContainerCmd = dockerClient.stopContainerCmd(containerId);
            stopContainerCmd.exec();
        } catch (NotFoundException e) {
            logger.error("停止容器失败。", e);
            throw new DockerContainerException("stopContainer-002", String.format("容器 %s 不存在。", containerId));
        } catch (NotModifiedException e) {
            logger.error("停止容器失败。", e);
            throw new DockerContainerException("stopContainer-003", String.format("容器 %s 已经停止。", containerId));
        } catch (Exception e) {
            logger.error("停止容器失败。", e);
            throw new DockerContainerException("stopContainer-004", String.format("停止容器 %s 失败。", containerId));
        }
    }

    //endregion

    //region 删除容器

    /**
     * 删除容器
     *
     * @param containerId
     */
    @Override
    public void removeContainer(String containerId) throws DockerContainerException {
        throwExceptionIfContainerIdIsNullOrEmpty("removeContainer-001", containerId);

        try {
            this.stopContainer(containerId);
            RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(containerId);
            removeContainerCmd.exec();
        } catch (NotFoundException e) {
            logger.error("删除容器失败。", e);
            throw new DockerContainerException("removeContainer-002", String.format("容器 %s 不存在。", containerId));
        } catch (Exception e) {
            logger.error("删除容器失败。", e);
            throw new DockerContainerException("removeContainer-003", String.format("删除容器 %s 失败。", containerId));
        }
    }

    //endregion 删除容器

    //region 重启容器

    /**
     * 重启容器
     *
     * @param containerId 容器标识
     */
    @Override
    public void restartContainer(String containerId) throws DockerContainerException {
        throwExceptionIfContainerIdIsNullOrEmpty("restartContainer-001", containerId);

        try {
            RestartContainerCmd restartContainerCmd = dockerClient.restartContainerCmd(containerId);
            restartContainerCmd.exec();
        } catch (NotFoundException e) {
            logger.error("重启容器失败。", e);
            throw new DockerContainerException("restartContainer-02", String.format("重启容器失败：容器 %s 不存在。", containerId));
        } catch (Exception e) {
            logger.error("重启容器失败。", e);
            throw new DockerContainerException("removeContainer-003", String.format("重启容器 %s 失败。", containerId));
        }
    }

    //endregion

    /**
     * @param containers
     */
    @Override
    public List<DockerContainer> createContainers(List<DockerCreateContainerCmd> containers) throws DockerContainerException {
        List<DockerContainer> result = new ArrayList<DockerContainer>();
        for (DockerCreateContainerCmd dockerCreateContainerCmd : containers) {
            DockerContainer container = this.createContainer(dockerCreateContainerCmd);
            result.add(container);
        }
        return result;
    }

    /**
     * 新建容器栈并且将新建的容器连接至新建的子网中
     *
     * @param containers  容器栈
     * @param netWorkName 子网名称
     */
    @Override
    public List<DockerContainer> createContainersWithNewNetWork(List<DockerCreateContainerCmd> containers, String netWorkName) throws DockerContainerException {
        this.createNewNetWork(netWorkName);

        return connectContainerWithNetWork(containers, netWorkName);
    }

    /**
     * 新建容器栈并且将新建的容器连接至指定标识的子网中
     *
     * @param containers 容器栈
     * @param netWorkId  子网标识
     */
    @Override
    public List<DockerContainer> createContainersWithNetWorkId(List<DockerCreateContainerCmd> containers, String netWorkId) throws DockerContainerException {
        this.throwExceptionIfNetWorkIdIsNullOrEmpty("createContainersWithNetWorkId-001", netWorkId);

        Network network = getNetworkById(netWorkId);

        return connectContainerWithNetWork(containers, network.getName());
    }

    /**
     * 新建容器栈并且将新建的容器连接至指定名称的子网中
     *
     * @param containers  容器栈
     * @param netWorkName 子网名称
     */
    @Override
    public List<DockerContainer> createContainersWithNetWorkName(List<DockerCreateContainerCmd> containers, String netWorkName) throws DockerContainerException {
        this.throwExceptionIfNetWorkNameIsNullOrEmpty("createContainersWithNetWorkName-001", netWorkName);

        Network network = getNetworkByName(netWorkName);

        return connectContainerWithNetWork(containers, network.getName());
    }

    //region private Method

    private Map<DockerPort, List<DockerPort>> buildPortBindings(InspectContainerResponse containerResponse) {
        Map<DockerPort, List<DockerPort>> result = new HashMap<DockerPort, List<DockerPort>>();
        HostConfig hostConfig = containerResponse.getHostConfig();
        if (hostConfig == null) {
            return result;
        }

        Ports ports = hostConfig.getPortBindings();
        if (ports == null) {
            return result;
        }

        Map<ExposedPort, Ports.Binding[]> bindings = ports.getBindings();
        if (CollectionUtils.isEmpty(bindings)) {
            return result;
        }

        for (Map.Entry<ExposedPort, Ports.Binding[]> binding : bindings.entrySet()) {
            ExposedPort exposedPort = binding.getKey();
            if (exposedPort == null) {
                continue;
            }
            Ports.Binding[] portsBinding = binding.getValue();
            if (portsBinding == null) {
                continue;
            }

            List<DockerPort> portsValue = new ArrayList<DockerPort>();
            for (Ports.Binding port : portsBinding) {
                portsValue.add(new DockerPort().setIp(port.getHostIp()).setPort(port.getHostPortSpec()));
            }
            DockerPort portsKey = new DockerPort().setPort(String.valueOf(exposedPort.getPort())).setProtocol(DockerProtocol.valueOf(binding.getKey().getProtocol().name()));
            result.put(portsKey, portsValue);
        }
        return result;
    }

    private List<DockerPort> buildExposedPorts(InspectContainerResponse containerResponse) {
        List<DockerPort> result = new ArrayList<DockerPort>();
        ContainerConfig containerConfig = containerResponse.getConfig();
        if (containerConfig == null) {
            return result;
        }
        ExposedPort[] exposedPorts = containerConfig.getExposedPorts();
        if (exposedPorts == null) {
            return result;
        }

        for (ExposedPort exposedPort : exposedPorts) {
            DockerPort dockerPort = new DockerPort().
                    setPort(String.valueOf(exposedPort.getPort())).
                    setProtocol(DockerProtocol.valueOf(exposedPort.getProtocol().name()));
            result.add(dockerPort);
        }
        return result;
    }

    private void createNewNetWork(String netWorkName) {
        try {
            this.dockerNetworkOperations.createNetWork(netWorkName);
        } catch (DockerNetWorkException e) {
            throw new DockerContainerException("createNewNetWork-001", e.getMessage());
        } catch (Exception e) {
            throw new DockerContainerException("createNewNetWork-002", "子网创建失败。");
        }
    }

    private Network getNetworkById(String netWorkId) {
        InspectNetworkCmd cmd = this.dockerClient.inspectNetworkCmd().withNetworkId(netWorkId);
        Network result = cmd.exec();
        if (result == null) {
            throw new DockerContainerException("createContainersWithNetWork-001", String.format("标志为 %s 的子网不存在。", netWorkId));
        }
        return result;
    }

    private void throwExceptionIfImageNameIsNullOrEmpty(String errorCode, String imageName) {
        if (StringUtils.isBlank(imageName)) {
            throw new DockerContainerException(errorCode, "镜像名称不能为NULL或空字符串。");
        }
    }

    private void throwExceptionIfCreateContainerCmdIsNull(DockerCreateContainerCommand createContainerCmd) {
        if (createContainerCmd == null) {
            throw new DockerContainerException("createContainer-001", "容器信息不能为NULL。");
        }
    }

    private void throwExceptionIfContainerIdIsNullOrEmpty(String errorCode, String containerId) {
        if (StringUtils.isBlank(containerId)) {
            throw new DockerContainerException(errorCode, "容器标识不能为NULL或空字符串。");
        }
    }

    private void throwExceptionIfNetWorkNameIsNullOrEmpty(String errorCode, String netWorkName) {
        if (StringUtils.isBlank(netWorkName)) {
            throw new DockerContainerException(errorCode, "子网名称不能为NULL或空字符串。");
        }
    }

    private void throwExceptionIfNetWorkIdIsNullOrEmpty(String errorCode, String netWorkId) {
        if (StringUtils.isBlank(netWorkId)) {
            throw new DockerContainerException(errorCode, "子网标识不能为NULL或者空字符串。");
        }
    }

    private List<DockerContainer> connectContainerWithNetWork(List<DockerCreateContainerCmd> containers, String networkName) {
        if (CollectionUtils.isEmpty(containers)) {
            throw new DockerContainerException("connectContainerWithNetWork-001", "新建容器栈失败：容器栈不能为空。");
        }
        List<DockerContainer> result = new ArrayList<DockerContainer>();
        for (DockerCreateContainerCmd dockerCreateContainerCmd : containers) {
            dockerCreateContainerCmd.withNetworkMode(networkName);
            DockerContainer container = this.createContainer(dockerCreateContainerCmd);
            try {
                Thread.sleep(intervalSecond * 1000);
            } catch (InterruptedException e) {
                throw new DockerContainerException("新建容器栈失败", e.getMessage());
            }
            result.add(container);
        }
        return result;
    }

    private Network getNetworkByName(String netWorkName) {
        ListNetworksCmd listNetworksCmd = this.dockerClient.listNetworksCmd().withNameFilter(netWorkName);
        List<Network> networks = listNetworksCmd.exec();
        if (CollectionUtils.isEmpty(networks)) {
            throw new DockerContainerException("createContainersWithNetWorkName-001", String.format("子网 %s 不存在。", netWorkName));
        }
        return networks.get(0);
    }

    private String getImageNameById(String imageId) {
        InspectImageResponse image = this.dockerClient.inspectImageCmd(imageId).exec();
        if (image == null) {
            return "";
        }
        List<String> repoTags = image.getRepoTags();
        if (CollectionUtils.isEmpty(repoTags)) {
            return "";
        }
        return repoTags.get(0);
    }

    //endregion private Method
}
