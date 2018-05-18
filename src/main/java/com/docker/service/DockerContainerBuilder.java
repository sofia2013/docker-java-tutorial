package com.docker.service;

import com.docker.model.DockerContainer;
import com.docker.model.DockerContainerState;
import com.docker.model.DockerPort;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author sofia
 */
public class DockerContainerBuilder {

    private DockerContainer dockerContainer;

    public DockerContainerBuilder(String containerId) {
        dockerContainer = new DockerContainer(containerId);
    }

    public DockerContainerBuilder setName(String name) {
        if (StringUtils.startsWith(name, "/")) {
            name = StringUtils.stripStart(name, "/");
        }
        dockerContainer.setName(name);
        return this;
    }

    public DockerContainerBuilder setStatus(DockerContainerState status) {
        dockerContainer.setStatus(status);
        return this;
    }

    public DockerContainerBuilder setImageId(String imageId) {
        dockerContainer.setImageId(imageId);
        return this;
    }

    public DockerContainerBuilder setCreated(String created) {
        dockerContainer.setCreated(created);
        return this;
    }

    public DockerContainerBuilder setExposedPorts(List<DockerPort> exposedPorts) {
        dockerContainer.setExposedPorts(exposedPorts);
        return this;
    }

    public DockerContainerBuilder setPortBindings(Map<DockerPort, List<DockerPort>> portBindings) {
        dockerContainer.setPortBindings(portBindings);
        return this;
    }

    public DockerContainerBuilder setImageName(String imageName) {
        dockerContainer.setImageName(imageName);
        return this;
    }

    public DockerContainer build() {
        return dockerContainer;
    }
}
