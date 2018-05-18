package com.docker.exception;

/**
 * @author sofia
 */
public class DockerContainerException extends RuntimeException {

    private final String code;

    public String getCode() {
        return code;
    }

    public DockerContainerException(String code, String message) {
        super(message);
        this.code = code;
    }
}
