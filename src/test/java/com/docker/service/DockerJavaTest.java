package com.docker.service;

import com.docker.BaseTestCase;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.EventsResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.dockerjava.core.util.CompressArchiveUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
 *
 */
public class DockerJavaTest extends BaseTestCase {

    @Autowired
    private DockerClient dockerClient;

    @Test
    public void 新建_启动_停止一个容器() {
        CreateContainerResponse container = dockerClient.createContainerCmd("tomcat").exec();
        dockerClient.startContainerCmd(container.getId()).exec();
        dockerClient.stopContainerCmd(container.getId()).exec();
    }


    @Test
    public void 下载本地仓库中的镜像() throws Exception {
        dockerClient.pullImageCmd("119.3.3.64:9005/hello-world:latest")
                .exec(new PullImageResultCallback())
                .awaitCompletion(30, TimeUnit.SECONDS);
    }

    @Test
    public void 下载官方仓库中的镜像() throws Exception {
        dockerClient.pullImageCmd("java:latest")
                .exec(new PullImageResultCallback())
                .awaitCompletion(30, TimeUnit.SECONDS);
    }

    @Test
    public void createContainerWithMountBindVolumes() {
        Volume volume1 = new Volume("/usr/webapps");
        Volume volume2 = new Volume("/usr/webapps");

        CreateContainerResponse container = dockerClient.createContainerCmd("tomcat")
                .withVolumes(volume1, volume2)
                .withBinds(new Bind("/home/app1", volume1, true), new Bind("/home/app2", volume2))
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();
    }

    @Test
    public void CreateContainerAndStartItWithExposedPorts() {
        ExposedPort tcp22 = ExposedPort.tcp(22);
        ExposedPort tcp23 = ExposedPort.tcp(23);

        Ports portBindings = new Ports();
        portBindings.bind(tcp22, Ports.Binding.bindPort(11022));
        portBindings.bind(tcp23, Ports.Binding.bindPort(11023));

        CreateContainerResponse container = dockerClient.createContainerCmd("busybox")
                .withCmd("true")
                .withExposedPorts(tcp22, tcp23)
                .withPortBindings(portBindings)
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();
    }


    @Test
    public void createContainerAndStartItWithAutoExposedPorts() {
        ExposedPort tcp22 = ExposedPort.tcp(22);
        ExposedPort tcp23 = ExposedPort.tcp(23);

        CreateContainerResponse container1 = dockerClient.createContainerCmd("busybox")
                .withCmd("sleep", "9999")
                .withExposedPorts(tcp22, tcp23)
                .exec();

        Ports portBindings = new Ports();
        portBindings.bind(tcp22, Ports.Binding.empty());
        portBindings.bind(tcp23, Ports.Binding.empty());

        CreateContainerResponse container2 = dockerClient
                .createContainerCmd("busybox")
                .withCmd("sleep", "9999")
                .withExposedPorts(tcp22, tcp23)
                .withPortBindings(portBindings)
                .withPublishAllPorts(true).exec();

        dockerClient.startContainerCmd(container2.getId()).exec();
    }

    @Test
    public void create2ContainersAndStartWithLinking() {
        CreateContainerResponse container1 = dockerClient.createContainerCmd("busybox")
                .withCmd("sleep", "9999")
                .withName("container1")
                .exec();

        dockerClient.startContainerCmd(container1.getId()).exec();

        CreateContainerResponse container2 = dockerClient.createContainerCmd("busybox")
                .withCmd("true")
                .withName("container2")
                .withLinks(new Link("container1", "container1Link"))
                .exec();

        dockerClient.startContainerCmd(container2.getId())
                .exec();
    }


    @Test
    public void Create2ContainersWithMountBindedVolumesAndInheritItsVolumesInAThirdContainer() {
        CreateContainerResponse container1 = dockerClient
                .createContainerCmd("busybox")
                .withCmd("sleep", "9999")
                .withName("container1")
                .withBinds(new Bind("/src/webapp1", new Volume("/opt/webapp1")))
                .exec();

        dockerClient.startContainerCmd(container1.getId()).exec();

        CreateContainerResponse container2 = dockerClient
                .createContainerCmd("busybox")
                .withCmd("sleep", "9999")
                .withName("container2")
                .withBinds(new Bind("/src/webapp2", new Volume("/opt/webapp2")))
                .exec();

        dockerClient.startContainerCmd(container2.getId()).exec();

        // create a third container with all volumes from container1 and container2
        CreateContainerResponse container3 = dockerClient
                .createContainerCmd("busybox")
                .withCmd("sleep", "9999")
                .withName("container3")
                .withVolumesFrom(new VolumesFrom("container1"), new VolumesFrom("container2"))
                .exec();

        dockerClient.startContainerCmd(container3.getId()).exec();
    }


    @Test
    public void HandleEvents() throws InterruptedException, IOException {
        EventsResultCallback callback = new EventsResultCallback() {
            @Override
            public void onNext(Event event) {
                System.out.println("Event: " + event);
                super.onNext(event);
            }
        };

        dockerClient.eventsCmd().exec(callback).awaitCompletion().close();
    }

    @Test
    public void buildImageFromDockerfile() {
        File baseDir = new File("~/kpelykh/docker/netcat");

        BuildImageResultCallback callback = new BuildImageResultCallback() {
            @Override
            public void onNext(BuildResponseItem item) {
                System.out.println("" + item);
                super.onNext(item);
            }
        };

        dockerClient.buildImageCmd(baseDir).exec(callback).awaitImageId();
    }

    @Test
    public void 创建并启动wordpress() {
        ExposedPort tcp80 = ExposedPort.tcp(80);
        Ports portBindings = new Ports();
        portBindings.bind(tcp80, Ports.Binding.bindPort(8080));

        CreateContainerResponse container = dockerClient.createContainerCmd("wordpress")
                .withExposedPorts(tcp80)
                .withPortBindings(portBindings)
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();
    }

    @Test
    public void 创建并启动mariadb() {
        String testVariable = "MYSQL_ROOT_PASSWORD=example";
        CreateContainerResponse container = dockerClient.createContainerCmd("mysql")
                .withEnv(testVariable)
                .exec();
        dockerClient.startContainerCmd(container.getId()).exec();
    }

    @Test
    public void createContainerWithLinkInCustomNetwork() throws DockerException {
        String[] env =
                {
                        "/home/docker/deploy/finance-network/logs:/usr/local/tomcat/logs",
                        "/home/docker/deploy/finance-network/upload-files:/home/docker/deploy/projects/finance/upload-files",
                        "/home/docker/deploy/finance-network/conf/context.xml:/usr/local/tomcat/conf/context.xml"};
        ExposedPort tcp8080 = ExposedPort.tcp(8080);
        Ports portBindings = new Ports();
        portBindings.bind(tcp8080, Ports.Binding.bindPort(9001));
        CreateContainerResponse container = dockerClient.createContainerCmd("finance-test:0.0.1")
                .withExposedPorts(tcp8080)
                .withPortBindings(portBindings)
                .withNetworkMode("net1")
                .withEnv(env)
                .withName("finance-net-java")
                .exec();
        dockerClient.startContainerCmd(container.getId()).exec();

        String testVariable = "MYSQL_ROOT_PASSWORD=root";
        CreateContainerResponse container2 = dockerClient.createContainerCmd("mysql:5.7")
                .withName("db-net-java")
                .withEnv(testVariable)
                .withNetworkMode("net1")
                .exec();
        dockerClient.startContainerCmd(container2.getId()).exec();
    }

    /*docker run -it -p 9002:8080 \
--restart=always \
--name finance-net \
--network net1 \
-v /home/docker/deploy/finance-network/logs:/usr/local/tomcat/logs \
-v /home/docker/deploy/finance-network/upload-files:/home/docker/deploy/projects/finance/upload-files \
-v /home/docker/deploy/finance-network/conf/context.xml:/usr/local/tomcat/conf/context.xml  \
finance-test:0.0.1*/


    @Test
    public void createdb() throws DockerException {
        CreateContainerResponse container2 = dockerClient.createContainerCmd("localhost:9005/ecm_cloud-mysql:1.0.0")
                .withName("ecm_cloud-db")
                .withNetworkMode("C1")
                .exec();
        dockerClient.startContainerCmd(container2.getId()).exec();
    }


    @Test
    public void createContainerWithLinkInCustomNetwork1() throws DockerException {
        Volume volume1 = new Volume("/etc/localtime:ro");
        CreateContainerResponse container = dockerClient.createContainerCmd("mysql:5.7")
                .withVolumes(volume1)
                .withBinds(new Bind("/etc/localtime", volume1, true))
                .withCmd("true")
                .withEnv("MYSQL_ROOT_PASSWORD=root")
                .exec();

        dockerClient.startContainerCmd(container.getId())
                .exec();
    }


    @Test
    public void createweb() throws DockerException {
        ExposedPort tcp8080 = ExposedPort.tcp(8080);
        Ports portBindings = new Ports();
        portBindings.bind(tcp8080, Ports.Binding.bindPort(9003));
        CreateContainerResponse container = dockerClient.createContainerCmd("localhost:9005/ecm_cloud:1.0.0")
                .withExposedPorts(tcp8080)
                .withPortBindings(portBindings)
                .withNetworkMode("C1")
                .withName("ecm_cloud-web")
                .withEnv("mysql.host=ecm_cloud-db",
                        "mysql.port=3306",
                        "application.database=ecm_cloud",
                        "application.user=ecm_cloud",
                        "application.password=ecm_cloud@Skytech18",
                        "edc.manager=http://192.168.1.152:8058/edcmng/",
                        "edc.storage=http://192.168.1.152:8057/storage/",
                        "time.switch=off",
                        "attachment.dir=/appdata/carbon_cloud/," +
                                "file_server=http://49.4.66.171:9900/cms_portal_fileserver")
                .exec();
        dockerClient.startContainerCmd(container.getId()).exec();
    }

    private String filePath = "E:\\docker-java-tutorials\\src\\test\\resources\\dockerfile";
    private String imageName = "localhost:9005/push-test:v1.0.0";

    @Test
    public void 制作镜像_1() {
        File baseDir = new File(filePath);
        Collection<File> files = FileUtils.listFiles(baseDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        try {
            File tarFile = CompressArchiveUtil.archiveTARFiles(baseDir, files, UUID.randomUUID().toString());
            dockerfileBuild(new FileInputStream(tarFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dockerfileBuild(InputStream tarInputStream, String dockerFilePath) {
        BuildImageCmd buildImageCmd = dockerClient.buildImageCmd()
                .withTags(new HashSet<String>(Arrays.asList(imageName)))
                .withTarInputStream(tarInputStream)
                .withDockerfilePath(dockerFilePath);
        execBuild(buildImageCmd);
    }

    private void dockerfileBuild(InputStream tarInputStream) {
        BuildImageCmd buildImageCmd = dockerClient.buildImageCmd()
                .withTags(new HashSet<String>(Arrays.asList(imageName)))
                .withTarInputStream(tarInputStream);
        execBuild(buildImageCmd);
    }

    private void dockerfileBuild(File baseDir) {
        BuildImageCmd buildImageCmd = dockerClient.buildImageCmd(baseDir)
                .withTags(new HashSet<String>(Arrays.asList(imageName)));
        execBuild(buildImageCmd);
    }

    private void execBuild(BuildImageCmd buildImageCmd) {
        String imageId = buildImageCmd.withNoCache(true).exec(new BuildImageResultCallback()).awaitImageId();

        try {
            dockerClient.pushImageCmd(imageName)
                    .withAuthConfig(dockerClient.authConfig())
                    .exec(new PushImageResultCallback())
                    .awaitCompletion(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
