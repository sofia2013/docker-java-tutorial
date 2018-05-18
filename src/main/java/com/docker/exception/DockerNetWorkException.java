package com.docker.exception;

/**
 * @author sofia
 */
public class DockerNetWorkException extends RuntimeException {

    private final String code;

    public String getCode() {
        return code;
    }

    public DockerNetWorkException(String code, String message) {
        super(message);
        this.code = code;
    }
}
