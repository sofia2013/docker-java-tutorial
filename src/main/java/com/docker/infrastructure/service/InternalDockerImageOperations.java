package com.docker.infrastructure.service;

import com.docker.exception.DockerImageException;
import com.docker.service.DockerImageOperations;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.dockerjava.core.util.CompressArchiveUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

@Service("dockerClient.dockerImageOperations")
public class InternalDockerImageOperations implements DockerImageOperations {

    @Value("${dockerClient.pushImagesAwaitCompletionSeconds:30}")
    private int pushImagesAwaitCompletionSeconds = 30;

    private static final Logger logger = LoggerFactory.getLogger(InternalDockerImageOperations.class);

    @Autowired
    private DockerClient dockerClient;

    @Value("${registryRepository:localhost:9005}")
    private String registryRepository = "localhost:9005";

    /**
     * @param dockerFilePath
     * @param imageRepository 仓库名为两段式路径，比如 jwilder/nginx-proxy
     * @param tag
     * @return
     */
    public String buildImage(String dockerFilePath, String imageRepository, String tag) throws DockerImageException {
        if (StringUtils.isBlank(imageRepository)) {
            throw new DockerImageException("buildImage-002", "镜像仓库名不能为NULL或者空字符串。");
        }
        if (StringUtils.isBlank(tag)) {
            throw new DockerImageException("buildImage-003", "镜像标签名不能为NULL或者空字符串。");
        }

        InputStream tarFile = fetchImageBuildFilesToTarFile(dockerFilePath);
        String imageName = String.format("%s/%s:%s", registryRepository, imageRepository, tag);
        return dockerfileBuild(tarFile, imageName);
    }


    @Override
    public void pushImage(String imageRepository, String tag) throws DockerImageException {
        String imageName = String.format("%s/%s:%s", registryRepository, imageRepository, tag);
        try {
            dockerClient.pushImageCmd(imageName)
                    .withAuthConfig(dockerClient.authConfig())
                    .exec(new PushImageResultCallback())
                    .awaitCompletion(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("推送镜像失败。", e);
            throw new DockerImageException("buildImage-006", "镜像制作文件读取失败。");
        }
    }

    private InputStream fetchImageBuildFilesToTarFile(String dockerFilePath) {
        if (StringUtils.isBlank(dockerFilePath)) {
            throw new DockerImageException("buildImage-001", "镜像制作文件目录不能为NULL或者空字符串。");
        }
        File dockerFile = new File(dockerFilePath);
        if (dockerFile.exists() == false) {
            throw new DockerImageException("buildImage-004", "镜像制作文件目录不存在。");
        }
        Collection<File> files = FileUtils.listFiles(dockerFile, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        if (CollectionUtils.isEmpty(files)) {
            throw new DockerImageException("buildImage-005", "镜像制作文件不存在。");
        }
        try {
            File file = CompressArchiveUtil.archiveTARFiles(dockerFile, files, UUID.randomUUID().toString());
            return new FileInputStream(file);
        } catch (IOException e) {
            logger.error("镜像制作文件读取失败。", e);
            throw new DockerImageException("buildImage-006", "镜像制作失败。");
        }
    }

    private String dockerfileBuild(InputStream inputStream, String imageName) {
        BuildImageCmd buildImageCmd = dockerClient
                .buildImageCmd()
                .withTags(new HashSet<String>(Arrays.asList(imageName)))
                .withTarInputStream(inputStream);
        return buildImageCmd.withNoCache(true).exec(new BuildImageResultCallback()).awaitImageId();
    }


}
