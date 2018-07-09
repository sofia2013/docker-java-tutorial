package com.docker.service;

import com.docker.BaseTestCase;
import com.docker.infrastructure.command.DockerCreateContainerCmd;
import com.docker.model.DockerRestartPolicy;
import com.docker.model.ImageRepository;
import com.docker.model.RegistryAuthConfig;
import com.github.dockerjava.api.model.AuthConfig;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DockerTest extends BaseTestCase {
    @Resource(name = "docker.dockerImageOperations")
    private DockerImageOperations dockerImageOperations;

    private String loginName = "admin";
    private String password = "Skynj123321";
    private String registryRepository = "harbor.test.com";

    private String imageName = "isv/carbon-edc";
    private String imageTag = "1.0.0";
    private ImageRepository imageRepository;
    private ImageRepository newImageNameWithRepository = new ImageRepository(imageName);

    private RegistryAuthConfig authConfig;

    @Before
    public void setUp() {
        imageRepository = new ImageRepository(imageName);
        imageRepository.setTag(imageTag);
        imageRepository.setRepository(registryRepository);

        newImageNameWithRepository = new ImageRepository(imageName);
        newImageNameWithRepository.setTag(imageTag);
        newImageNameWithRepository.setRepository("localhost:9005");

        authConfig = new RegistryAuthConfig(loginName, password, registryRepository);
    }

    @Test
    public void 审核人员同步开发者镜像仓库中指定的镜像_拉取镜像_01() {
        dockerImageOperations.pullImageFrom(imageRepository, authConfig);
    }

    @Test
    public void 审核人员同步开发者镜像仓库中指定的镜像_重命名镜像_02() {
        dockerImageOperations.tagImage(imageRepository, newImageNameWithRepository);
    }

    @Test
    public void 审核人员同步开发者镜像仓库中指定的镜像_推送镜像_03() {
        dockerImageOperations.pushImageToLocal(newImageNameWithRepository);
    }

    @Resource(name = "docker.dockerContainerOperations")
    private DockerContainerOperations dockerContainerOperations;

    //

    /**
     * 数据库名称，如：dppt
     */
    private String dbName = "edc_mng";

    /**
     * 数据库用户名
     */
    String dbUser = "edc_mng";

    /**
     * 数据库用户名密码
     */
    String dbPassword = "edc_mng_p@ssw0rd";
    /**
     * 租户标识
     */
    private String renterName = "companyA";
    /**
     * 应用标识，如：电票平台
     */
    private String appName = "edc-mng-web";
    /**
     * 租户子网名称，如：XXX企业
     */
    private String netWorkName = "C1";

    /**
     * 宿主机端口
     */
    private int hostPort = 9007;

    /**
     * 容器端口
     */
    private int defaultContainerExposePort = 8080;
    /**
     * 数据库镜像名称
     */
    private String db_ImageName = "localhost:9005/isv/carbon-edc-mysql:1.0.0";
    /**
     * 一次订阅的唯一标识
     */
    private long currentTime = System.currentTimeMillis();
    /**
     * 应用镜像的名称
     */
    private String webImageName = "localhost:9005/isv/carbon-edc:1.0.0";
    /**
     *
     */
    private String mysqlContainerName = String.format("%s_%s_%s", appName, "db", currentTime);

    @Test
    public void 企业订阅应用_04() {
        List<DockerCreateContainerCmd> containers = new ArrayList<DockerCreateContainerCmd>();

        DockerCreateContainerCmd mysql = createContainerMysql();
        DockerCreateContainerCmd web = createContainerWeb();
        containers.add(mysql);
        containers.add(web);
        dockerContainerOperations.createContainersWithNetWorkName(containers, netWorkName);
    }

    private DockerCreateContainerCmd createContainerMysql() {
        DockerCreateContainerCmd result = new DockerCreateContainerCmd(db_ImageName);
        result.setName(mysqlContainerName);
        result.withRestartPolicy(DockerRestartPolicy.ALWAYS_RESTART);

        return result;
    }

    private DockerCreateContainerCmd createContainerWeb() {
        String[] envs = {
                "mysql.host=" + mysqlContainerName,
                "mysql.port=3306",
                "application.database=" + dbName,
                "application.user=" + dbUser,
                "application.password=" + dbPassword,
                "pagination.enforce=false",
                "pagination.size=50",
                "application.name=com.skytech.manage",
                "simulate=false",
                "edc.storage=http://localhost:8090/storage/",
                "ent_code=732252465",
                "ent_name=中盐常州化工",
                "ent_city=常州市",
                "application.fileRootPath=",
        };

        DockerCreateContainerCmd web = new DockerCreateContainerCmd(webImageName);
        web.setName(appName + "_app_" + currentTime);
        web.setExposedPorts(defaultContainerExposePort);
        web.setPortBindings(new HashMap<Integer, Integer>() {
            {
                put(defaultContainerExposePort, hostPort);
            }
        });
        web.setEnv(envs);
        return web;
    }
}
