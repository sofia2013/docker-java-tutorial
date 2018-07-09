package com.docker.infrastructure.service;

import com.docker.exception.DockerImageException;
import com.docker.model.ImageRepository;
import com.docker.model.RegistryAuthConfig;
import com.docker.service.DockerImageOperations;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.dockerjava.core.util.CompressArchiveUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Docker镜像操作服务
 */
public class InternalDockerImageOperations implements DockerImageOperations {

    private static final Logger logger = LoggerFactory.getLogger(InternalDockerImageOperations.class);
    private DockerClient dockerClient;
    private long pullImagesAwaitSeconds = 30;
    private long pushImagesAwaitSeconds = 30;

    public InternalDockerImageOperations setPullImagesAwaitSeconds(long pullImagesAwaitSeconds) {
        if (pullImagesAwaitSeconds <= 0) {
            throw new DockerImageException("pullImage-init", String.format("镜像拉取延迟时间（%秒）不合法。", pullImagesAwaitSeconds));
        }
        this.pullImagesAwaitSeconds = pullImagesAwaitSeconds;
        return this;
    }

    public InternalDockerImageOperations setPushImagesAwaitSeconds(long pushImagesAwaitSeconds) {
        if (pullImagesAwaitSeconds <= 0) {
            throw new DockerImageException("pullImage-init", String.format("镜像推动延迟时间（%秒）不合法。", pushImagesAwaitSeconds));
        }
        this.pushImagesAwaitSeconds = pushImagesAwaitSeconds;
        return this;
    }

    public InternalDockerImageOperations(DockerClient dockerClient) {
        if (dockerClient == null) {
            throw new DockerImageException("pullImage-init", String.format("docker连接器不能为NULL或空字符串。"));
        }
        this.dockerClient = dockerClient;
    }

    @Override
    public void pullImageFrom(ImageRepository imageNameWithRepository, RegistryAuthConfig registryAuthConfig) throws DockerImageException {
        if (imageNameWithRepository == null) {
            throw new DockerImageException("pullImageFrom-001", "镜像地址不能为NULL。");
        }

        if (registryAuthConfig == null) {
            throw new DockerImageException("pullImageFrom-002", "镜像仓库认证信息不能为NULL。");
        }

        AuthConfig authConfig = new AuthConfig()
                .withUsername(registryAuthConfig.getUsername())
                .withPassword(registryAuthConfig.getPassword())
                .withRegistryAddress(registryAuthConfig.getRegistryAddress());

        String imageName = imageNameWithRepository.toString();
        try {
            dockerClient.pullImageCmd(imageName).
                    withAuthConfig(authConfig).
                    exec(new PullImageResultCallback()).
                    awaitCompletion(pullImagesAwaitSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new DockerImageException("pullImageFrom-003", String.format("下载镜像 %s 失败。", imageName));
        }
    }

    @Override
    public void pullImage(ImageRepository imageNameWithRepository) throws DockerImageException {
        if (imageNameWithRepository == null) {
            throw new DockerImageException("pullImage-001", "镜像地址不能为NULL。");
        }
        this.pullImage(imageNameWithRepository.toString());
    }

    @Override
    public void pullImage(String imageNameWithRepository) throws DockerImageException {
        try {
            dockerClient
                    .pullImageCmd(imageNameWithRepository)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion(pullImagesAwaitSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new DockerImageException("pullImage-002", String.format("下载镜像 %s 失败。", imageNameWithRepository));
        }
    }

    @Override
    public String buildImage(String dockerFilePath, ImageRepository imageNameWithRepository) throws DockerImageException {
        if (imageNameWithRepository == null) {
            throw new DockerImageException("buildImage-002", "镜像仓库名不能为NULL或者空字符串。");
        }

        String imageName = imageNameWithRepository.toString();
        InputStream tarFile = fetchImageBuildFilesToTarFile(dockerFilePath);
        return dockerfileBuild(tarFile, imageName);
    }

    @Override
    public void pushImageToLocal(ImageRepository imageNameWithRepository) throws DockerImageException {
        if (imageNameWithRepository == null) {
            throw new DockerImageException("pushImageToLocal-001", "镜像地址不能为NULL。");
        }

        String imageName = imageNameWithRepository.toString();
        try {
            dockerClient.pushImageCmd(imageName).
                    withAuthConfig(dockerClient.authConfig()).
                    exec(new PushImageResultCallback()).
                    awaitCompletion(pushImagesAwaitSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("推送镜像失败。", e);
            throw new DockerImageException("pushImageToLocal-002", "镜像制作文件读取失败。");
        } catch (Exception e) {
            logger.error("推送镜像失败。", e);
            throw new DockerImageException("pushImageToLocal-003", String.format("推送镜像 %s 失败。", imageName));
        }
    }

    @Override
    public void tagImage(ImageRepository imageNameWithRepository, ImageRepository newImageNameWithRepository) throws DockerImageException {
        if (imageNameWithRepository == null) {
            throw new DockerImageException("tagImage-001", "原镜像地址不能为NULL。");
        }
        if (newImageNameWithRepository == null) {
            throw new DockerImageException("tagImage-002", "新镜像地址不能为NULL。");
        }

        dockerClient.tagImageCmd(imageNameWithRepository.toString(), newImageNameWithRepository.toStringWithoutTag(), newImageNameWithRepository.getTag()).exec();
    }

    private InputStream fetchImageBuildFilesToTarFile(String dockerFilePath) {
        if (StringUtils.isBlank(dockerFilePath)) {
            throw new DockerImageException("fetchImageBuildFilesToTarFile-001", "镜像制作文件目录不能为NULL或者空字符串。");
        }
        File dockerFile = FileUtils.getFile(dockerFilePath);
        if (dockerFile.exists() == false) {
            throw new DockerImageException("fetchImageBuildFilesToTarFile-002", "镜像制作文件目录不存在。");
        }
        Collection<File> files = FileUtils.listFiles(dockerFile, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        if (CollectionUtils.isEmpty(files)) {
            throw new DockerImageException("fetchImageBuildFilesToTarFile-003", "镜像制作文件不存在。");
        }
        try {
            File file = CompressArchiveUtil.archiveTARFiles(dockerFile, files, UUID.randomUUID().toString());
            return new FileInputStream(file);
        } catch (IOException e) {
            logger.error("镜像制作文件读取失败。", e);
            throw new DockerImageException("fetchImageBuildFilesToTarFile-004", "镜像制作失败。");
        }
    }

    private String dockerfileBuild(InputStream inputStream, String imageName) {
        BuildImageCmd buildImageCmd = dockerClient
                .buildImageCmd()
                .withTags(new HashSet(Arrays.asList(imageName)))
                .withTarInputStream(inputStream);
        return buildImageCmd.withNoCache(true).exec(new BuildImageResultCallback()).awaitImageId();
    }

}
