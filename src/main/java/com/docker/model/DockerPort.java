package com.docker.model;

/**
 *
 * @author sofia
 */
public class DockerPort {
    private String ip;
    private String port;
    private DockerProtocol protocol;

    public String getIp() {
        return ip;
    }

    public DockerPort setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getPort() {
        return port;
    }

    public DockerPort setPort(String port) {
        this.port = port;
        return this;
    }

    public DockerProtocol getProtocol() {
        return protocol;
    }

    public DockerPort setProtocol(DockerProtocol protocol) {
        this.protocol = protocol;
        return this;
    }
}
