package com.docker.model;

import org.apache.commons.lang.StringUtils;

/**
 * @author sofia
 */

public enum DockerRestartPolicy {

    /**
     * 默认策略，在容器退出时不重启容器
     */
    NO_RESTART("no", 0),
    /**
     * 在容器退出时总是重启容器
     */
    ALWAYS_RESTART("always", 0),
    /**
     * 在容器退出时总是重启容器，但是不考虑在Docker守护进程启动时就已经停止了的容器
     */
    UNLESS_STOPPED_RESTART("unless-stopped", 0),
    /**
     * 在容器非正常退出时（退出状态非0），才会重启容器，可设置重启次数
     */
    ON_FAILURE_RESTART("on-failure", 0);

    private String name;

    private int maximumRetryCount = 0;

    DockerRestartPolicy(String name, int maximumRetryCount) {
        this.name = name;
        this.maximumRetryCount = maximumRetryCount;
    }

    public String getName() {
        return name;
    }

    public int getMaximumRetryCount() {
        return maximumRetryCount;
    }

    /**
     * 容器异常退出后重启次数上限，只有ON_FAILURE_RESTART有效
     *
     * @param maximumRetryCount 容器异常退出后重启次数上限
     * @return
     */
    public DockerRestartPolicy setMaximumRetryCount(int maximumRetryCount) {
        if (StringUtils.equals(this.name, "on-failure") == false) {
            return this;
        }
        this.maximumRetryCount = maximumRetryCount;
        return this;
    }

    public static DockerRestartPolicy nameOf(String name) {
        if ("no".equals(name)) {
            return NO_RESTART;
        }
        if ("always".equals(name)) {
            return ALWAYS_RESTART;
        }
        if ("unless-stopped".equals(name)) {
            return UNLESS_STOPPED_RESTART;
        }
        if ("on-failure".equals(name)) {
            return ON_FAILURE_RESTART;
        }
        return NO_RESTART;
    }
}
