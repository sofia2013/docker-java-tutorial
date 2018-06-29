package com.docker.service;

import com.docker.BaseTestCase;
import com.docker.exception.DockerContainerException;
import com.docker.infrastructure.command.DockerCreateContainerCmd;
import com.docker.model.DockerContainer;
import com.docker.model.DockerContainerState;
import com.docker.model.DockerRestartPolicy;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DockerClientOperationsTest extends BaseTestCase {

    @Autowired
    private DockerContainerOperations dockerContainerOperations;

    @Test
    public void 查找一个不存在的容器() {
        DockerContainer result = dockerContainerOperations.inspectAContainer("foo");
        Assert.assertNull(result);
    }

    @Test
    public void 创建一个容器_本地仓库已有镜像_本地未下载镜像() {
        DockerContainer result = dockerContainerOperations.createASimpleContainer("119.3.3.64:9005/hello-world:latest");
        Assert.assertNotNull(result);
    }

    @Test
    public void 创建一个容器_官方仓库已有镜像_本地未下载镜像() {
        DockerContainer result = dockerContainerOperations.createASimpleContainer("hello-world:latest");
        Assert.assertNotNull(result);
    }

    @Test
    public void 创建容器_停止容器() {
        DockerContainer container = dockerContainerOperations.createASimpleContainer("tomcat:latest");
        String containerId = container.getContainerId();

        DockerContainer result = dockerContainerOperations.inspectAContainer(containerId);
        Assert.assertNotNull(result);
        Assert.assertEquals(DockerContainerState.RUNNING, result.getStatus());

        dockerContainerOperations.stopContainer(containerId);

        result = dockerContainerOperations.inspectAContainer(containerId);
        Assert.assertNotNull(result);
        Assert.assertEquals(DockerContainerState.EXITED, result.getStatus());
    }

    @Test
    public void 重启一个容器() {
        DockerContainer container = dockerContainerOperations.createASimpleContainer("tomcat");
        String containerId = container.getContainerId();

        dockerContainerOperations.restartContainer(containerId);

        DockerContainer result = dockerContainerOperations.inspectAContainer(containerId);
        Assert.assertEquals(DockerContainerState.RUNNING, result.getStatus());
    }

    @Test
    public void 删除一个容器() {
        DockerContainer container = dockerContainerOperations.createASimpleContainer("tomcat");

        String containerId = container.getContainerId();

        DockerContainer result = dockerContainerOperations.inspectAContainer(containerId);
        Assert.assertNotNull(result);

        dockerContainerOperations.removeContainer(containerId);

        result = dockerContainerOperations.inspectAContainer(containerId);
        Assert.assertNull(result);
    }

    @Test
    public void 新建并启动一个简易容器() {
        DockerContainer result = dockerContainerOperations.createASimpleContainer("hello-world");
        Assert.assertNotNull(result);
    }

    @Test
    public void 新建并启动一个容器() {
        DockerCreateContainerCmd createContainerCmd = new DockerCreateContainerCmd("tomcat");
        createContainerCmd.setName("tomcat-8081-8082-8083");
        createContainerCmd.setExposedPorts(8081, 8082, 8089);
        dockerContainerOperations.createContainer(createContainerCmd);
    }

    @Test
    public void 新建并启动容器_端口_映射宿主机() {
        ExposedPort tcp80 = ExposedPort.tcp(80);
        Ports portBindings = new Ports();
        portBindings.bind(tcp80, Ports.Binding.bindPort(8080));

        DockerCreateContainerCmd tomcat = new DockerCreateContainerCmd("wordpress");
        tomcat.setExposedPorts(80);
        tomcat.setPortBindings(new HashMap<Integer, Integer>() {
            {
                put(80, 8080);
            }
        });

        dockerContainerOperations.createContainer(tomcat);
    }

    @Test
    public void 新建并启动容器_环境变量() {
        String env = "MYSQL_ROOT_PASSWORD=example";
        DockerCreateContainerCmd db = new DockerCreateContainerCmd("mariadb");
        db.setEnv(env);

        dockerContainerOperations.createContainer(db);
    }

    @Test
    public void 创建容器栈_使用Link参数连接() {
        DockerCreateContainerCmd db = buildContainerA();
        DockerCreateContainerCmd myWordPress = buildContainerB();

        List<DockerCreateContainerCmd> containers = new ArrayList();
        containers.add(db);
        containers.add(myWordPress);


        dockerContainerOperations.createContainers(containers);
    }

    @Test
    public void 创建容器栈_加入已有的子网中() {
        List<DockerCreateContainerCmd> containers = new ArrayList<DockerCreateContainerCmd>();

        final String volume1 = "/usr/local/tomcat/logs";
        final String volume2 = "/home/docker/deploy/projects/finance/upload-files";
        final String volume3 = "/usr/local/tomcat/conf/context.xml";
        List<String> volumes = new ArrayList<String>();
        volumes.add(volume1);
        volumes.add(volume2);
        volumes.add(volume3);

        DockerCreateContainerCmd web = new DockerCreateContainerCmd("finance-test:0.0.1");
        web.withNetworkMode("net1");
        web.setName("web-net1");
        web.setExposedPorts(8080);
        web.setPortBindings(new HashMap<Integer, Integer>() {
            {
                put(8080, 9008);
            }
        });
        web.withVolumes(volumes);
        web.withBinds(new HashMap<String, String>() {
            {
                put("/home/docker/deploy/finance-net1/logs", volume1);
                put("/home/docker/deploy/finance-net1/upload-files", volume2);
                put("/home/docker/deploy/finance-net1/conf/context.xml", volume3);
            }
        });

        containers.add(web);
        dockerContainerOperations.createContainersWithNetWorkName(containers, "net1");
    }

    @Test
    public void 创建容器栈_创建子网_tomcat数据库配置静态存在() {
        List<DockerCreateContainerCmd> containers = new ArrayList<DockerCreateContainerCmd>();

        final String volume1 = "/usr/local/tomcat/logs";
        final String volume2 = "/home/docker/deploy/projects/finance/upload-files";
        final String volume3 = "/usr/local/tomcat/conf/context.xml";
        List<String> volumes = new ArrayList<String>();
        volumes.add(volume1);
        volumes.add(volume2);
        volumes.add(volume3);

        DockerCreateContainerCmd web = new DockerCreateContainerCmd("finance-test:0.0.1");
        web.withNetworkMode("net2");
        web.setName("web-net2");
        web.setExposedPorts(8080);
        web.setPortBindings(new HashMap<Integer, Integer>() {
            {
                put(8080, 9002);
            }
        });
        web.withVolumes(volumes);
        web.withBinds(new HashMap<String, String>() {
            {
                put("/home/docker/deploy/finance-net2/logs", volume1);
                put("/home/docker/deploy/finance-net2/upload-files", volume2);
                put("/home/docker/deploy/finance-net2/conf/context.xml", volume3);
            }
        });
        containers.add(web);

        DockerCreateContainerCmd db = new DockerCreateContainerCmd("db-net");
        db.setName("db-net2");
        db.setEnv("MYSQL_ROOT_PASSWORD=root");

        containers.add(db);
        dockerContainerOperations.createContainersWithNewNetWork(containers, "net2");
    }

    @Test
    public void 创建容器栈_创建子网_tomcat数据库配置动态生成() {
        List<DockerCreateContainerCmd> containers = new ArrayList<DockerCreateContainerCmd>();

        DockerCreateContainerCmd web = createWebContainer();
        DockerCreateContainerCmd db = createDBContainer();

        containers.add(web);
        containers.add(db);
        dockerContainerOperations.createContainersWithNewNetWork(containers, "net" + web.getName() + "_" + db.getName());
    }

    private DockerCreateContainerCmd createDBContainer() {
        DockerCreateContainerCmd db = new DockerCreateContainerCmd("db-net");
        db.setName("db-net3");
        db.setEnv("MYSQL_ROOT_PASSWORD=root1");
        return db;
    }

    private DockerCreateContainerCmd createWebContainer() {
        final String volume1 = "/usr/local/tomcat/logs";
        final String volume2 = "/home/docker/deploy/projects/finance/upload-files";
        final String volume3 = "/usr/local/tomcat/conf/context.xml";

        List<String> volumes = new ArrayList<String>();
        volumes.add(volume1);
        volumes.add(volume2);
        volumes.add(volume3);

        HashMap<String, String> binds = new HashMap<String, String>() {
            {
                put("/home/docker/deploy/finance-net2/logs", volume1);
                put("/home/docker/deploy/finance-net2/upload-files", volume2);
                put("/home/docker/deploy/finance-net2/conf/context.xml", volume3);
            }
        };

        String[] envs = {
                "mysql.password=skynj123321",
                "mysql.username=root",
                "mysql.url=jdbc:mysql://db-net3:3306/db?characterEncoding=utf-8&amp;serverTimezone=GMT%2b8"};

        DockerCreateContainerCmd web = new DockerCreateContainerCmd("finance-test:0.0.1");
        web.setName("web-net3");
        web.setExposedPorts(8080);
        web.setPortBindings(new HashMap<Integer, Integer>() {
            {
                put(8080, 9003);
            }
        });
        web.withVolumes(volumes);
        web.withBinds(binds);
        web.setEnv(envs);
        return web;
    }

    @Test(expected = DockerContainerException.class)
    public void 未提供镜像名称_空字符串() {
        dockerContainerOperations.createASimpleContainer(" ");
    }

    @Test(expected = DockerContainerException.class)
    public void 未提供镜像名称_null() {
        dockerContainerOperations.createASimpleContainer(null);
    }

    @Test(expected = DockerContainerException.class)
    public void 新建并启动一个重名的容器() {
        DockerCreateContainerCmd createContainerCmd = new DockerCreateContainerCmd("hello-world");
        createContainerCmd.setName("foo");
        dockerContainerOperations.createContainer(createContainerCmd);

        createContainerCmd = new DockerCreateContainerCmd("hello-world");
        createContainerCmd.setName("foo");
        dockerContainerOperations.createContainer(createContainerCmd);
    }

    @Test
    public void 新建并启动一个容器_创建容器数据卷_随机挂载宿主机目录() {
        List<String> volumes = new ArrayList<String>();
        volumes.add("/volume1");
        volumes.add("/volumn2");

        DockerCreateContainerCommand dockerContainer = new DockerCreateContainerCmd("tomcat");
        dockerContainer.setName("volume");
        dockerContainer.withVolumes(volumes);

        dockerContainerOperations.createContainer(dockerContainer);
    }

    private DockerCreateContainerCmd buildContainerB() {
        DockerCreateContainerCmd myWordPress = new DockerCreateContainerCmd("wordpress");
        myWordPress.setName("mywordpress");
        myWordPress.setLinks(new HashMap<String, String>() {
            {
                put("dbLinkName", "db_alias");
            }
        });
        myWordPress.setExposedPorts(80);
        myWordPress.setPortBindings(new HashMap<Integer, Integer>() {
            {
                put(80, 9999);
            }
        });
        return myWordPress;
    }

    private DockerCreateContainerCmd buildContainerA() {
        String env = "MYSQL_ROOT_PASSWORD=example";
        DockerCreateContainerCmd db = new DockerCreateContainerCmd("mariadb");
        db.setName("dbLinkName");
        db.setEnv(env);
        return db;
    }

    @Test
    public void 设置重启策略(){
        DockerCreateContainerCmd tomcat = new DockerCreateContainerCmd("tomcat:latest");
        tomcat.withRestartPolicy(DockerRestartPolicy.ALWAYS_RESTART);
        dockerContainerOperations.createContainer(tomcat);
    }


    //助贸集成测试

    /**
     * 数据库名称，如：dppt
     */
    private String dbName = "db";
    /**
     * 租户标识
     */
    private String renterName = "companyA";
    /**
     * 应用标识，如：电票平台
     */
    private String appName = "finance";
    /**
     * 租户子网名称，如：XXX企业
     */
    private String netWorkName = "companyA";

    /**
     * 宿主机端口
     */
    private int hostPort = 9002;

    /**
     * 容器端口
     */
    private int defaultContainerExposePort = 8080;
    /**
     * 数据库镜像名称
     */
    private String db_ImageName ="localhost:9005/finance-mysql:1.0.0";
    /**
     * 一次订阅的唯一标识
     */
    private long currentTime = System.currentTimeMillis();
    /**
     * 应用镜像的名称
     */
    private String webImageName = "localhost:9005/finance-web:1.0.0";
    /**
     * 容器内部数据卷参数，如日志目录
     */
    private final String web_container_logPath = "/usr/local/tomcat/logs";
    /**
     * 容器内部数据卷参数，如附件上传路径
     */
    private final String web_container_uploadPath = "/home/docker/deploy/projects/finance/upload-files";
    /**
     * 挂载在宿主机的数据卷参数，如日志目录
     */
    private String web_hostPath_logPath = String.format("/home/docker/deploy/%s/%s/%s/logs", appName, renterName, currentTime);

    /**
     * 挂载在宿主机的数据卷参数，如附件上传路径
     */
    private String web_hostPath_uploadPath = String.format("/home/docker/deploy/%s/%s/%s/upload-files", appName, renterName, currentTime);

    /**
     * 容器内数据存放地址
     */
    private String db_data_path = "/var/lib/mysql";

    /**
     * 宿主机数据存放映射地址
     */
    private String db_data_hostPath = String.format("/home/docker/deploy/%s/%s/dbdata/%s", appName, renterName, currentTime);

    @Test
    public void 创建容器栈_子网存在_助贸() {
        List<DockerCreateContainerCmd> containers = new ArrayList<DockerCreateContainerCmd>();

        DockerCreateContainerCmd container1 = createContainer1();
        DockerCreateContainerCmd container2 = createContainer2(container1.getName());
        containers.add(container1);
        containers.add(container2);
        dockerContainerOperations.createContainersWithNetWorkName(containers, netWorkName);
    }

    /**
     * @return
     */
    private DockerCreateContainerCmd createContainer1() {
        DockerCreateContainerCmd result = new DockerCreateContainerCmd(db_ImageName);
        List<String> volumes = new ArrayList<String>();

        volumes.add(db_data_path);

        String containerName = String.format("%s_%s_%s_%s", appName, renterName, "db", currentTime);
        result.setName(containerName);
        result.withVolumes(volumes);
        result.setEnv("MYSQL_ROOT_PASSWORD=skynj123321");
        result.withBinds(new HashMap<String, String>() {
            {

                put(db_data_hostPath, db_data_path);
            }
        });
        return result;
    }

    private DockerCreateContainerCmd createContainer2(String dbContainerName) {
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
                "mysql.password=skynj123321",
                "mysql.username=root",
                "mysql.url=jdbc:mysql://" + dbContainerName + ":3306/" + dbName + "?characterEncoding=utf-8&amp;serverTimezone=GMT%2b8",
                "application.database=" + dbName,
                "client_ip=127.0.0.1",
                "client_port=10010",
                "admin_password=abc1234",
                "app_user_token=financing_user_token",
                "public_encrypt_key=test",
                "private_encrypt_key=test",
                "upload_url=http://dev.test.com:8080/multiUploadFile",
                "download_url=http://dev.test.com:8080/downloadFile",
                "upload_root_path=" + web_container_uploadPath,
                "aliyun.accessKeyId=1",
                "aliyun.accessKeySecret=1",
                "aliyun.systemName=天商助贸大数据服务dev"
        };

        DockerCreateContainerCmd web = new DockerCreateContainerCmd(webImageName);
        web.setName(appName + "_" + renterName + "_app_" + currentTime);
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
