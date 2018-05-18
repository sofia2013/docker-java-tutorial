package com.docker.model;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author sofia
 */
public enum DockerContainerState {

    CREATED("created", 0) {
        @Override
        public DockerContainerState nameOf(String name) {
            if (StringUtils.equals(name, "created")) {
                return DockerContainerState.CREATED;
            }
            return null;
        }
    },
    RESTARTING("restarting", 1) {
        @Override
        public DockerContainerState nameOf(String name) {
            if (StringUtils.equals(name, "restarting")) {
                return DockerContainerState.RESTARTING;
            }
            return null;
        }
    },
    RUNNING("running", 2) {
        @Override
        public DockerContainerState nameOf(String name) {
            if (StringUtils.equals(name, "running")) {
                return DockerContainerState.RUNNING;
            }
            return null;
        }
    },
    PAUSED("paused", 3) {
        @Override
        public DockerContainerState nameOf(String name) {
            if (StringUtils.equals(name, "paused")) {
                return DockerContainerState.PAUSED;
            }
            return null;
        }
    },
    EXITED("exited", 4) {
        @Override
        public DockerContainerState nameOf(String name) {
            if (StringUtils.equals(name, "exited")) {
                return DockerContainerState.EXITED;
            }
            return null;
        }
    };

    private final String name;

    private final int code;

    DockerContainerState(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public abstract DockerContainerState nameOf(String name);
}
