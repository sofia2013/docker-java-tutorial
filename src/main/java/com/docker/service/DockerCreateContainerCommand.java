package com.docker.service;

import com.docker.model.DockerRestartPolicy;

import java.util.List;
import java.util.Map;

/**
 * @author sofia
 */
public interface DockerCreateContainerCommand {

    Object[] getBinds();

    Integer getCpuPeriod();

    String getCpusetCpus();

    String getCpusetMems();

    Integer getCpuShares();

    String[] getEnv();

    String getNetworkMode();

    Object[] getExposedPorts();

    Object getPortBindings();

    String getImage();

    Object[] getLinks();

    DockerRestartPolicy getRestartPolicy();

    Long getMemory();

    Long getMemorySwap();

    String getName();

    Object[] getVolumes();

    Boolean getPrivileged();

    DockerCreateContainerCommand setName(String name);

    DockerCreateContainerCommand setImage(String image);

    DockerCreateContainerCommand withBinds(Map<String, String> binds);

    DockerCreateContainerCommand setEnv(String... env);

    DockerCreateContainerCommand setLinks(Map<String, String> links);

    DockerCreateContainerCommand withPrivileged(Boolean privileged);

    DockerCreateContainerCommand withVolumes(List<String> volumePaths);

    DockerCreateContainerCommand setExposedPorts(Integer... exposedPorts);

    DockerCreateContainerCommand withNetworkMode(String networkName);

    DockerCreateContainerCommand setExposedPorts(List<Integer> exposedPorts);

    DockerCreateContainerCommand setPortBindings(Map<Integer, Integer> portBindings);

    /**
     * 设置重启策略
     * @param restartPolicy
     * @return
     */
    DockerCreateContainerCommand withRestartPolicy(DockerRestartPolicy restartPolicy);

    DockerCreateContainerCommand withCpuPeriod(Integer cpuPeriod);

    DockerCreateContainerCommand withCpusetCpus(String cpusetCpus);

    DockerCreateContainerCommand withCpusetMems(String cpusetMems);

    DockerCreateContainerCommand withCpuShares(Integer cpuShares);

    DockerCreateContainerCommand withMemory(Long memory);

    DockerCreateContainerCommand withMemorySwap(Long memorySwap);
}
