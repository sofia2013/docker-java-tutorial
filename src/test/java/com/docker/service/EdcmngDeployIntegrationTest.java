package com.docker.service;

import com.docker.BaseTestCase;
import com.docker.infrastructure.command.DockerCreateContainerCmd;
import com.docker.model.DockerRestartPolicy;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EdcmngDeployIntegrationTest extends BaseTestCase {

    @Autowired
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
    private String db_ImageName = "localhost:9005/edc-mysql:test";
    /**
     * 一次订阅的唯一标识
     */
    private long currentTime = System.currentTimeMillis();
    /**
     * 应用镜像的名称
     */
    private String webImageName = "localhost:9005/edc-mng:1.0.0";
    /**
     * 容器内部数据卷参数，如日志目录
     */
    private final String web_container_logPath = "/usr/local/tomcat/logs";
    /**
     * 容器内部数据卷参数，如附件上传路径
     */
    private final String web_container_uploadPath = "/home/wangfu/edc_files";
    /**
     * 挂载在宿主机的数据卷参数，如日志目录
     */
    private String web_hostPath_logPath = String.format("/home/docker/apps/%s/%s/%s/logs", appName, renterName, currentTime);

    /**
     * 挂载在宿主机的数据卷参数，如附件上传路径
     */
    private String web_hostPath_uploadPath = String.format("/home/docker/apps/%s/%s/%s/attachment", appName, renterName, currentTime);

    /**
     * 容器内数据存放地址
     */
    private String db_data_path = "/var/lib/mysql";

    /**
     * 宿主机数据存放映射地址
     */
    private String db_data_hostPath = String.format("/home/docker/apps/%s/%s/dbdata/%s", appName, renterName, currentTime);


    /**
     *
     */
    private String mysqlContainerName = String.format("%s_%s_%s", appName, "db", currentTime);

    /**
     *
     */
    @Test
    public void 创建容器栈_子网存在() {
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
        result.withBinds(new HashMap<String, String>() {
            {

                put(db_data_hostPath, db_data_path);
            }
        });
        /**
         * 设置容器的自启动策略，详情请看DockerRestartPolicy说明
         */
        result.withRestartPolicy(DockerRestartPolicy.ON_FAILURE_RESTART.setMaximumRetryCount(4));

        return result;
    }

    private DockerCreateContainerCmd createContainerWeb() {
        List<String> volumes = new ArrayList<String>();
        volumes.add(web_container_logPath);
        volumes.add(web_container_uploadPath);

        HashMap<String, String> volumesBinds = new HashMap<String, String>() {
            {
                put(web_hostPath_logPath, web_container_logPath);
                put(web_hostPath_uploadPath, web_container_uploadPath);
            }
        };

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
                "application.fileRootPath=" + web_container_uploadPath,
        };

        DockerCreateContainerCmd web = new DockerCreateContainerCmd(webImageName);
        web.setName(appName + "_app_" + currentTime);
        web.setExposedPorts(defaultContainerExposePort);
        web.setPortBindings(new HashMap<Integer, Integer>() {
            {
                put(defaultContainerExposePort, hostPort);
            }
        });
        web.withVolumes(volumes);
        web.withBinds(volumesBinds);
        web.setEnv(envs);
        return web;

    }

}
