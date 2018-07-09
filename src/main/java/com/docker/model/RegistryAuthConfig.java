package com.docker.model;

public class RegistryAuthConfig {

    private static final String DEFAULT_SERVER_ADDRESS = "http://%s/v2/";

    private String username;

    private String password;

    private String registryAddress;

    public RegistryAuthConfig(String username, String password, String registryAddress) {
        this.username = username;
        this.password = password;
        this.registryAddress = String.format(DEFAULT_SERVER_ADDRESS, registryAddress);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public String getRegistryAddress() {
        return registryAddress;
    }
}
