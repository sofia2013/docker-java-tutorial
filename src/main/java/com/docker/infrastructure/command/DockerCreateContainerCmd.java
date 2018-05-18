package com.docker.infrastructure.command;

import com.docker.exception.DockerContainerException;
import com.docker.model.DockerRestartPolicy;
import com.docker.service.DockerCreateContainerCommand;
import com.github.dockerjava.api.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sofia
 */
public class DockerCreateContainerCmd implements DockerCreateContainerCommand {

    /**
     * 容器名称
     */
    private String name;

    /**
     * 环境变量
     */
    private String[] env;

    /**
     * 镜像名称
     */
    private String image;

    /**
     * 数据卷
     */
    private Volumes volumes = new Volumes();

    /**
     * 容器端口
     */
    private ExposedPorts exposedPorts = new ExposedPorts();

    private HostConfig hostConfig = new HostConfig();

    /**
     * 请输入完整的镜像名称
     *
     * @param imageName 如：tomcat
     * @param tag       如：8.0
     */
    public DockerCreateContainerCmd(String imageName, String tag) {
        checkNotNull(imageName, "镜像名称不能为NUll或者空字符串。");
        checkNotNull(tag, "镜像版本不能为NUll或者空字符串。");
        setImage(String.format("%:%", imageName, tag));
    }

    /**
     * 请输入完整的镜像名称，如：tomcat:8.0
     *
     * @param imageName
     */
    public DockerCreateContainerCmd(String imageName) {
        setImage(imageName);
    }

    @Override
    public Object[] getBinds() {
        return hostConfig.getBinds();
    }

    @Override
    public Integer getCpuPeriod() {
        return hostConfig.getCpuPeriod();
    }

    @Override
    public String getCpusetCpus() {
        return hostConfig.getCpusetCpus();
    }

    @Override
    public String getCpusetMems() {
        return hostConfig.getCpusetMems();
    }

    @Override
    public Integer getCpuShares() {
        return hostConfig.getCpuShares();
    }

    @Override
    public String[] getEnv() {
        return env;
    }

    @Override
    public String getNetworkMode() {
        return hostConfig.getNetworkMode();
    }

    @Override
    public Object[] getExposedPorts() {
        return exposedPorts.getExposedPorts();
    }

    @Override
    public Object getPortBindings() {
        return hostConfig.getPortBindings();
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public Object[] getLinks() {
        return hostConfig.getLinks();
    }

    @Override
    public Long getMemory() {
        return hostConfig.getMemory();
    }

    @Override
    public Long getMemorySwap() {
        return hostConfig.getMemorySwap();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DockerRestartPolicy getRestartPolicy() {
        if (hostConfig.getRestartPolicy() == null) {
            return DockerRestartPolicy.NO_RESTART;
        }
        return DockerRestartPolicy.nameOf(hostConfig.getRestartPolicy().getName()).setMaximumRetryCount(hostConfig.getRestartPolicy().getMaximumRetryCount());
    }

    @Override
    public Object[] getVolumes() {
        return volumes.getVolumes();
    }

    @Override
    public Boolean getPrivileged() {
        return hostConfig.getPrivileged();
    }

    @Override
    public DockerCreateContainerCmd withBinds(Map<String, String> bindList) {
        checkNotNull(bindList, "binds was not specified");
        List<Bind> binds = new ArrayList<Bind>();
        for (Map.Entry<String, String> bind : bindList.entrySet()) {
            binds.add(new Bind(bind.getKey(), new Volume(bind.getValue())));
        }
        hostConfig.setBinds(binds.toArray(new Bind[binds.size()]));
        return this;
    }

    @Override
    public DockerCreateContainerCmd withCpuPeriod(Integer cpuPeriod) {
        checkNotNull(cpuPeriod, "cpuPeriod was not specified");
        hostConfig.withCpuPeriod(cpuPeriod);
        return this;
    }

    @Override
    public DockerCreateContainerCmd withCpusetCpus(String cpusetCpus) {
        checkNotNull(cpusetCpus, "cpusetCpus was not specified");
        hostConfig.withCpusetCpus(cpusetCpus);
        return this;
    }

    @Override
    public DockerCreateContainerCmd withCpusetMems(String cpusetMems) {
        checkNotNull(cpusetMems, "cpusetMems was not specified");
        hostConfig.withCpusetMems(cpusetMems);
        return this;
    }

    @Override
    public DockerCreateContainerCmd withCpuShares(Integer cpuShares) {
        checkNotNull(cpuShares, "cpuShares was not specified");
        hostConfig.withCpuShares(cpuShares);
        return this;
    }

    @Override
    public DockerCreateContainerCmd setEnv(String... env) {
        checkNotNull(env, "env was not specified");
        this.env = env;
        return this;
    }

    @Override
    public DockerCreateContainerCmd setImage(String image) {
        checkNotNull(image, "no image was specified");
        this.image = image;
        return this;
    }

    @Override
    public DockerCreateContainerCmd setLinks(Map<String, String> linkList) {
        checkNotNull(linkList, "links was not specified");
        List<Link> links = new ArrayList<Link>();
        for (Map.Entry<String, String> link : linkList.entrySet()) {
            links.add(new Link(link.getKey(), link.getValue()));
        }

        this.hostConfig.setLinks(links.toArray(new Link[links.size()]));
        return this;
    }

    @Override
    public DockerCreateContainerCmd withMemory(Long memory) {
        checkNotNull(memory, "memory was not specified");
        hostConfig.withMemory(memory);
        return this;
    }

    @Override
    public DockerCreateContainerCmd withMemorySwap(Long memorySwap) {
        checkNotNull(memorySwap, "memorySwap was not specified");
        hostConfig.withMemorySwap(memorySwap);
        return this;
    }

    @Override
    public DockerCreateContainerCmd setName(String name) {
        checkNotNull(name, "name was not specified");
        this.name = name;
        return this;
    }

    @Override
    public DockerCreateContainerCmd setExposedPorts(Integer... exposedPortList) {
        checkNotNull(exposedPorts, "exposedPorts was not specified");
        List<ExposedPort> exposedPorts = new ArrayList<ExposedPort>();
        for (Integer exposedPort : exposedPortList) {
            exposedPorts.add(new ExposedPort(exposedPort));
        }

        this.exposedPorts = new ExposedPorts(exposedPorts);
        return this;
    }

    @Override
    public DockerCreateContainerCommand withNetworkMode(String networkName) {
        checkNotNull(networkName, "networkMode was not specified");
        this.hostConfig.withNetworkMode(networkName);
        return this;
    }

    @Override
    public DockerCreateContainerCmd setExposedPorts(List<Integer> exposedPortList) {
        checkNotNull(exposedPorts, "exposedPorts was not specified");
        List<ExposedPort> exposedPorts = new ArrayList<ExposedPort>();
        for (Integer exposedPort : exposedPortList) {
            exposedPorts.add(new ExposedPort(exposedPort));
        }

        this.exposedPorts = new ExposedPorts(exposedPorts);
        return this;
    }

    /**
     * 容器和宿主机的端口映射
     *
     * @param portBindingMap
     * @return
     */
    @Override
    public DockerCreateContainerCmd setPortBindings(Map<Integer, Integer> portBindingMap) {
        Ports portBindings = new Ports();
        for (Map.Entry<Integer, Integer> entry : portBindingMap.entrySet()) {
            ExposedPort tcp = ExposedPort.tcp(entry.getKey());
            portBindings.bind(tcp, Ports.Binding.bindPort(entry.getValue()));
        }
        checkNotNull(portBindings, "portBindings was not specified");
        this.hostConfig.withPortBindings(portBindings);
        return this;
    }

    @Override
    public DockerCreateContainerCmd withPrivileged(Boolean privileged) {
        checkNotNull(privileged, "no privileged was specified");
        this.hostConfig.withPrivileged(privileged);
        return this;
    }

    @Override
    public DockerCreateContainerCmd withRestartPolicy(DockerRestartPolicy restartPolicy) {
        checkNotNull(restartPolicy, "restartPolicy was not specified");
        this.hostConfig.withRestartPolicy(RestartPolicy.parse(restartPolicy.getName() + ":" + restartPolicy.getMaximumRetryCount()));
        return this;
    }

    @Override
    public DockerCreateContainerCmd withVolumes(List<String> volumePaths) {
        checkNotNull(volumes, "volumes was not specified");
        List<Volume> volumes = new ArrayList<Volume>();
        for (String volumePath : volumePaths) {
            volumes.add(new Volume(volumePath));
        }
        this.volumes = new Volumes(volumes.toArray(new Volume[volumes.size()]));
        return this;
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new DockerContainerException("DockerCreateContainerCmd-error。", String.valueOf(errorMessage));
        } else {
            return reference;
        }
    }
}
