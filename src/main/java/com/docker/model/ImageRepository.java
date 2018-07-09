package com.docker.model;

import com.docker.exception.DockerImageException;
import org.apache.commons.lang.StringUtils;

/**
 * @author sofia
 * 镜像地址
 */
public class ImageRepository {
    /**
     * 镜像名称，如:skynj/app1，不能为空
     */
    private String name;
    /**
     * 仓库地址，如:localhost:9000，可以为空
     */
    private String repository;
    /**
     * 版本号，如：1.0.0，默认为:latest
     */
    private String tag = "latest";

    public ImageRepository(String name) {
        if (StringUtils.isBlank(name)) {
            throw new DockerImageException("ImageRepository-001", "镜像名称不能为NULL或空字符串。");
        }
        this.name = name;
    }

    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new DockerImageException("ImageRepository-001", "镜像名称不能为NULL或空字符串。");
        }
        this.name = name;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public String getRepository() {
        return repository;
    }

    public String getTag() {
        return tag;
    }

    /**
     * 完整地址，不能为空，格式为：[repository]/[name]:[tag]，如：localhost:9005/skynj/app1:1.0.0
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (StringUtils.isBlank(this.repository) == false) {
            result.append(this.repository);
            result.append("/");
        }
        result.append(name);
        if (StringUtils.isBlank(tag) == false) {
            result.append(":");
            result.append(tag);
        }
        return result.toString();
    }

    /**
     * 无版本地址，不能为空，格式为：[repository]/[name]，如：localhost:9005/skynj/app1
     *
     * @return
     */
    public String toStringWithoutTag() {
        StringBuilder result = new StringBuilder();
        if (StringUtils.isBlank(this.repository) == false) {
            result.append(this.repository);
            result.append("/");
        }
        result.append(name);
        return result.toString();
    }
}
