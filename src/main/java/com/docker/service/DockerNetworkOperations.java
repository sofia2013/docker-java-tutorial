package com.docker.service;

import com.docker.exception.DockerNetWorkException;

/**
 * @author sofia
 */
public interface DockerNetworkOperations {

    /**
     * @param name
     * @return
     */
    void createNetWork(String name) throws DockerNetWorkException;


    /**
     * @param name
     * @return
     */
    Boolean networkExistsByName(String name);

}
