package com.docker.model;

/**
 * @author sofia
 */

public enum DockerProtocol {
    TCP("no", 0);

    private final String name;

    private final int code;

    DockerProtocol(String name, int code) {
        this.name = name;
        this.code = code;
    }

}
