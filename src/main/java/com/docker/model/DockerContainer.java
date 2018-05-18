package com.docker.model;

import java.util.List;
import java.util.Map;

/**
 * @author sofia
 */
public class DockerContainer {

    private final String containerId;

    private String name;

    private DockerContainerState status;

    private String imageId;

    private String created;

    private List<DockerPort> exposedPorts;

    private Map<DockerPort, List<DockerPort>> portBindings;

    private String imageName;

    public DockerContainer(String containerId) {
        this.containerId = containerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(DockerContainerState status) {
        this.status = status;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setExposedPorts(List<DockerPort> exposedPorts) {
        this.exposedPorts = exposedPorts;
    }

    public void setPortBindings(Map<DockerPort, List<DockerPort>> portBindings) {
        this.portBindings = portBindings;
    }

    public String getContainerId() {
        return containerId;
    }

    public String getName() {
        return name;
    }

    public DockerContainerState getStatus() {
        return status;
    }

    public String getImageId() {
        return imageId;
    }

    public String getCreated() {
        return created;
    }

    public List<DockerPort> getExposedPorts() {
        return exposedPorts;
    }

    public Map<DockerPort, List<DockerPort>> getPortBindings() {
        return portBindings;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }
}
