package com.docker.exception;

/**
 * @author sofia
 */
public class DockerImageException extends RuntimeException {

    private final String code;

    public String getCode() {
        return code;
    }

    public DockerImageException(String code, String message) {
        super(message);
        this.code = code;
    }
}
