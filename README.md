# Micro微服务
#### 微服务(SpringBoot+Dubbo) + 容器化(Docker) + 自动化部署(Jenkins)
---
#### 项目工具
* IntelliJ IDEA 2018.1.5 x64
* VMware Workstation Pro
##### 软件版本号
* mysql 5.7.22
* spring-boot 2.0.6.RELEASE
* jdk 1.8
* tomcat 8
* dubbo com.alibaba.spring.boot 2.0.0
* zookeeper 3.4.6
* zkclient com.101tec 0.10
* flywaydb 5.1.4
##### Docker相关
* ![](https://github.com/Charm-J/micro/blob/master/image/1.png)

* ![](https://github.com/Charm-J/micro/blob/master/image/2.png)
---

#### What can I learn？
从0搭建一整套微服务系统。

#### What is Micro-Service?
* 微服务架构
整个应用程序将会被拆分成一个个功能独立的子系统，可独立运行；
系统与系统之间通过RPC接口通信。这样系统间的耦合度大大降低，易扩展。
* 容器化部署
各个微服务将采用Docker来实现容器化部署，避免一切因环境引起的各种问题。
* 自动化构建
项目被微服务化后，各个服务之间的关系错综复杂，打包构建费事费力。通过借助Jenkins，实现一键自动化部署。

#### Why use them?
* 为什么用微服务：
系统架构的演进过程：单机结构->集群结构->微服务结构。
微服务结构好处有很多：
系统之间的耦合度大大降低，可以独立开发、独立部署、独立测试，系统与系统之间的边界非常明确，排错也变得相当容易，开发效率大大提升。
系统之间的耦合度降低，从而系统更易于扩展。我们可以针对性地扩展某些服务。假设这个商城要搞一次大促，下单量可能会大大提升，因此我们可以针对性地提升订单系统、产品系统的节点数量，而对于后台管理系统、数据分析系统而言，节点数量维持原有水平即可。
服务的复用性更高。比如，当我们将用户系统作为单独的服务后，该公司所有的产品都可以使用该系统作为用户系统，无需重复开发。
* 为什么需要Dubbo：
减少运维的复杂度：能够针对性地增加某些服务的处理能力（某些服务的背后可能是一个集群模式）。
* 为什么需要Docker：
Docker不仅能够实现运行环境的隔离，而且能极大程度的节约计算机资源，它成为一种轻量级的“虚拟机”。
* 为什么需要Jenkins：
针对多个系统，不仅依赖关系复杂，而且模块构建顺序有所讲究，每次人工操作耗时耗力，还容易出错。


#### Actual-Training!
 
##### 1. 项目的组织结构  

创建一个Maven Project，命名为“micro”  

这个Project由多个Module构成，每个Module对应着“微服务”的一个子系统，
可独立运行，是一个独立的项目。 这也是目前主流的项目组织形式，即多模块项目。
在micro项目下创建各个子模块，每个自模块都是一个独立的SpringBoot项目：
```
micro-controller 请求调度控制层
micro-user 用户服务
micro-message 消息服务
micro-common-api 公用接口-被所有模块依赖
```
![](https://github.com/Charm-J/micro/blob/master/image/3.png)

* 创建Project  

New一个Project
选择Spring Initializr
设置groupId、artifactId、version

* 创建Module  

在Project上New Module  
![](https://github.com/Charm-J/micro/blob/master/image/4.png)  

和刚才一样，选择Spring Initializr，设置groupId、artifactId、version
依次创建好所有的Module，如下图所示： 
![](https://github.com/Charm-J/micro/blob/master/image/5.png)
* 构建模块的依赖关系  
目前为止，模块之间没有任何联系，下面我们要通过pom文件来指定它们之间的依赖关系。
![](https://github.com/Charm-J/micro/blob/master/image/6.png)

需要将micro-common-api打成jar包，并让这些模块依赖这个jar。
此外，为了简化各个模块的配置，我们将所有模块的通用依赖放在Project的pom文件中，
然后让所有模块作为Project的子模块。这样子模块就可以从父模块中继承所有的依赖，
而不需要自己再配置了。  

---
实操记录：  

首先将micro-common-api的打包方式设成jar
当打包这个模块的时候，Maven会将它打包成jar，并安装在本地仓库中。
这样其他模块打包的时候就可以引用这个jar。
```
<groupId>com.jeff</groupId>
<artifactId>micro-common-api</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>jar</packaging>
```


将其他模块的打包方式设为war
除了micro-common-api外，其他模块都是一个个可独立运行的子系统，
需要在web容器中运行，所以我们需要将这些模块的打包方式设成war
```
<groupId>com.jeff</groupId>
<artifactId>micro-user</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>war</packaging>
```
在总pom中指定子模块
modules标签指定了当前模块的子模块是谁，但是仅在父模块的pom文件中指定子模块还不够，
还需要在子模块的pom文件中指定父模块是谁。
```
<modules>
    <module>micro-controller</module>
    <module>micro-common-api</module>
    <module>micro-user</module>
    <module>micro-message</module>
</modules>
```
在子模块中指定父模块
```
<parent>
    <groupId>com.jeff</groupId>
    <artifactId>micro</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```
到此为止，模块的依赖关系配置完毕！但要注意模块打包的顺序。由于所有模块都依赖于micro-common-api模块，因此在构建模块时，首先需要编译、打包、安装micro-common-api，将它打包进本地仓库中，这样上层模块才能引用到。当该模块安装完毕后，再构建上层模块。
* 在父模块的pom中添加所有子模块公用的依赖

当父模块的pom中配置了公用依赖后，子模块的pom文件将非常简洁，如下所示：
```
<groupId>com.jeff</groupId>
<artifactId>micro-user</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>war</packaging>

<name>micro-user</name>
<description>User for Spring Boot</description>

<parent>
    <groupId>com.jeff</groupId>
    <artifactId>micro</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
</parent>

<build>
    <finalName>micro-user</finalName>
</build>
```
当项目的结构搭建完成之后，接下来你需要配置Docker环境，并将这些项目打包进容器中，验证下是否能正常启动。

##### 2. 创建Docker容器
以创建micro-user容器为例，采用如下命令创建容器：
```
docker run --privileged=true --name micro-user -p 8002:8080 -v /opt/micro/tomcat/logs:/usr/local/tomcat/logs  -v /opt/micro/tomcat/conf/tomcat-users.xml:/usr/local/tomcat/conf/tomcat-users.xml -v /opt/micro/tomcat/conf/context.xml:/usr/local/tomcat/webapps/manager/META-INF/context.xml -d docker.io/tomcat:8
```
_--name_：指定容器的名字  

_-p_：指定容器的端口映射 -p 8082:8080 表示将容器的8080端口映射到宿主机的8082端口上  

_-v_：指定容器数据卷的映射 xxx:yyy 表示将容器yyy目录映射到宿主机的xxx目录上，从而访问宿主机的xxx目录就相当于访问容器的yyy目录。  

docker.io/tomcat:8：表示容器所对应的镜像。  

第二个 _-v_ 解决cargo远程自动部署Jekins报错403的问题-涉及到的文件中有IP限制
这条命令执行成功后，你就可以通过你的IP:8082 访问到micro-user容器的tomcat了。如果你看到了那只眼熟了猫，那就说明容器启动成功了！ 
![](https://github.com/Charm-J/micro/blob/master/image/7.png)
接下来，你需要按照上面的方法，给剩下几个系统创建好Tomcat容器。

##### 3. 整合Dubbo
* 创建zookeeper容器
Dubbo一共定义了三种角色，分别是：服务提供者、服务消费者、注册中心。注册中心是服务提供者和服务消费者的桥梁，服务消费者会在初始化的时候将自己的IP和端口号发送给注册中心，而服务消费者通过注册中心知道服务提供者的IP和端口号。
* 父pom文件中引入dubbo和ZK的依赖
```
<!-- Dubbo -->
<dependency>
    <groupId>com.alibaba.spring.boot</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>2.0.0</version>
</dependency>
<!-- zookeeper -->
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.6</version>
    <exclusions>
        <exclusion>
            <artifactId>slf4j-log4j12</artifactId>
            <groupId>org.slf4j</groupId>
        </exclusion>
    </exclusions>
</dependency>
<!-- ZooKeeper client -->
<dependency>
    <groupId>com.101tec</groupId>
    <artifactId>zkclient</artifactId>
    <version>0.10</version>
</dependency>
```

##### 4. 自动化构建
Jenkins是一个自动化构建工具，它可以帮助我们摆脱繁琐的部署过程，我们只需要在一开始配置好构建策略，以后部署只需要一键完成。
* 创建Jenkins容器
Jenkins采用Java开发，也需要Java环境，但我们使用Docker后，一切都采用容器化部署，Jenkins也不例外。
拉取镜像
这里我们使用Jenkins官方提供的镜像，大家只需执行如下命令拉取即可：
`docker pull docker.io/jenkins/jenkins`

启动容器
需要注意：两个默认端口号不能随意抛弃。_-v_ docker内jekins数据卷映射到宿主机上
```
docker run -d --privileged=true --name jenkins  -u root  -p 9002:8080 -p 50000:50000  -v /opt/micro/jenkins:/var/jenkins_home docker.io/jenkins
```

初始化Jenkins
然后你需要访问IP:9002，Jenkins会带着你进行一系列的初始化设置，你只要跟着它一步步执行就OK  

* Jenkins插件安装
Jenkins线上安装插件需要连接谷歌，没有默认安装，可通过搜索安装：  

依次点击 系统管理->插件管理-> 可选插件-> 过滤搜索框中 查找如下插件：
```
Maven Integration plugin
SSH plugin
Deploy to container Plugin
GitHub Integration Plugin
```

* 在Jenkins中创建项目
点击页面左侧的“新建”按钮：<br>
![](https://github.com/Charm-J/micro/blob/master/image/8.png) <br>
输入项目名称micro-user，选择“构建一个Maven项目”，然后点击“OK”：
配置Git仓库
选择Git，然后输入本项目Git仓库的URL，并在Credentials中输入Git的用户名和密码，
构建触发器
选择第一项，如下图所示： 
![](https://github.com/Charm-J/micro/blob/master/image/9.png)
Pre Step
Pre Step会在正式构建前执行，由于所有项目都依赖于micro-common-api，因此在项目构建前，需要将它安装到本地仓库，然后才能被当前项目正确依赖。 因此，在Pre Step中填写如下信息： 
![](https://github.com/Charm-J/micro/blob/master/image/10.png)
Build
然后就是正式构建的过程，填写如下信息即可： 
![](https://github.com/Charm-J/micro/blob/master/image/11.png)
OK，Jenkins服务和micro-user服务并不在同一个Docker容器中，那么究竟该如何才能将Jenkins本地编译好的war包发送到micro-userr容器中呢？这就需要使用Jenkins的一个插件——Deploy Plugin  


* 远程部署
直接在插件页面搜索Deploy to container安装。
在父项目的pom文件中增加远程部署插件：
```
<plugin>
	<groupId>org.codehaus.cargo</groupId>
	<artifactId>cargo-maven2-plugin</artifactId>
	<version>1.6.5</version>
	<configuration>
		<container>
			<!-- 指明使用的tomcat服务器版本 -->
			<containerId>tomcat8</containerId>
			<type>remote</type>
		</container>
		<configuration>
			<type>runtime</type>
			<cargo.remote.username>Tomcat的用户名</cargo.remote.username>
			<cargo.remote.password>Tomcat的密码</cargo.remote.password>
		</configuration>
	</configuration>
	<executions>
		<execution>
			<phase>deploy</phase>
			<goals>
				<goal>redeploy</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```
为Tomcat设置用户名和密码
![](https://github.com/Charm-J/micro/blob/master/image/12.png)
修改Jenkins中micro-user的配置
在“构建后操作”中增加如下配置： 
![](https://github.com/Charm-J/micro/blob/master/image/13.png)
WAR/EAR files：表示你需要发布的war包  

Containers：配置目标Tomcat的用户名和密码  


##### 5. Maven的profile功能
在实际开发中，我们的系统往往有多套环境构成，如：开发环境、测试环境、预发环境、生产环境。而不同环境的配置各不相同。如果我们只有一套配置，那么当系统从一个环境迁移到另一个环境的时候，就需要通过修改代码来更换配置，这样无疑增加了工作的复杂度，而且易于出错。但好在Maven提供了profile功能，能帮助我们解决这一个问题。
父项目的pom中添加profile元素
首先，我们需要在总pom的中添加多套环境的信息，如下所示：
```
<profiles>
	<profile>
		<id>dev</id>
		<properties>
			<profileActive>dev</profileActive>
		</properties>
		<activation>
			<activeByDefault>true</activeByDefault>
		</activation>
	</profile>
	<profile>
		<id>test</id>
		<properties>
			<profileActive>test</profileActive>
		</properties>
	</profile>
	<profile>
		<id>prod</id>
		<properties>
			<profileActive>prod</profileActive>
		</properties>
	</profile>
</profiles>
```
父项目的pom中添加resource元素
resource标识了不同环境下需要打包哪些配置文件。
```
<resources>
	<resource>
	    <!-- 标识配置文件所在的目录 -->
		<directory>src/main/resources</directory>
		<filtering>true</filtering>
		<!-- 构建时将这些配置文件全都排除掉 -->
		<excludes>
			<exclude>application.properties</exclude>
			<exclude>application-dev.properties</exclude>
			<exclude>application-test.properties</exclude>
			<exclude>application-prod.properties</exclude>
		</excludes>
	</resource>
	<resource>
		<directory>src/main/resources</directory>
		<filtering>true</filtering>
		<!-- 标识构建时所需要的配置文件 -->
		<includes>
			<include>application.properties</include>
			<!-- ${profileActive}这个值会在maven构建时传入 -->
			<include>application-${profileActive}.properties</include>
		</includes>
	</resource>
</resources>
```
父项目的pom中添加插件maven-resources-plugin
该插件用来在Maven构建时参数替换
```
<plugin>
	<artifactId>maven-resources-plugin</artifactId>
	<version>3.0.2</version>
	<configuration>
		<delimiters>
			<delimiter>@</delimiter>
		</delimiters>
		<useDefaultDelimiters>false</useDefaultDelimiters>
	</configuration>
</plugin>
```
在子项目中创建配置
分别为dev环境、test环境、prod环境创建三套配置，application.proerpties中存放公用的配置。 
在application.properties中添加spring.profiles.active=@profileActive@
`spring.profiles.active=@profileActive@`

修改Jenkins的配置
在所有Jenkins中所有Maven命令的末尾添加 *-P test*，在打包的时候-P后面的参数将会作为@profileActive@的值传入系统中，从而根据该值打包相应的application-{profileActive}.properties文件。
##### 6. 开发流程
到此为止，所有准备工作都已经完成，接下来就可以进入代码开发阶段。下面我以一个例子，带着大家感受下有了这套微服务框架后，我们的开发流程究竟有了哪些改变？下面以开发一个用户登录功能为例，介绍下使用本框架之后开发的流程。

* 开发登录服务
首先需要在 micro-common-api 中创建UserService接口，并在其中声明登录的抽象函数。
![](https://github.com/Charm-J/micro/blob/master/image/14.png)

然后在micro-user中开发UserService的实现——UserServiceImpl。 UserServiceImpl上必须要加上Dubbo的*@Service*注解，从而告诉Dubbo，在本项目初始化的时候需要将这个类发布成一项服务，供其他系统调用。
![](https://github.com/Charm-J/micro/blob/master/image/15.png)

* 引用登录服务
当UserService开发完毕后，接下来micro-controller需要引用该服务，并向前端提供一个登录的REST接口。 若要使用userService中的函数，仅需要在userService上添加*@Reference*注解，然后就像调用本地函数一样使用userService即可。Dubbo会帮你找到UserService服务所在的IP和端口号，并发送调用请求。但这一切对于程序猿来说是完全透明的。
![](https://github.com/Charm-J/micro/blob/master/image/16.png)

* 自动构建服务
上面的代码完成后，接下来你需要将代码提交至你的Git仓库。接下来就是自动化部署的过程了。
你需要进入Jenkins，由于刚才修改了micro-user和micro-controller的代码，
因此你需要分别构建这两个项目。 
接下来Jenkins会自动从你的Git仓库中拉取最新的代码，
然后依次执行Pre Step、Build、构建后操作的过程。
由于我们在Pre Step中设置了编译micro-common-api，
因此Jenkins首先会将其安装到本地仓库；然后再执行Build过程，
构建micro-user，并将其打包成war包。
最后将执行“构建后操作”，将war包发布到相应的tomcat容器中。 
至此，整个发布流程完毕！

* 查看服务的状态
当Jenkins构建完成后，我们可以登录Dubbo-Admin查看服务发布和引用的状态。
当我们搜索UserService服务后，可以看到，该服务的提供者已经成功发布了服务： 
![](https://github.com/Charm-J/micro/blob/master/image/17.png)
点击“消费者”我们可以看到，该服务已经被controller-consumer成功订阅： 
![](https://github.com/Charm-J/micro/blob/master/image/18.png)
jekins 成功部署后
可以浏览器访问：http://192.168.109.128:8001/micro-controller/user/sayHello?name=2222
测试服务调用是否成功！<br>
![](https://github.com/Charm-J/micro/blob/master/image/19.png)

#### docker启动相关服务指令汇总
* miro-controller > tomcat启动
```
docker run --privileged=true --name micro-controller -p 8001:8080 -v /opt/micro/tomcat/logs:/usr/local/tomcat/logs  -v /opt/micro/tomcat/conf/tomcat-users.xml:/usr/local/tomcat/conf/tomcat-users.xml -v /opt/micro/tomcat/conf/context.xml:/usr/local/tomcat/webapps/manager/META-INF/context.xml -d docker.io/tomcat:8
```
* micro-user  > tomcat启动
```
docker run --privileged=true --name micro-user -p 8002:8080 -v /opt/micro/tomcat/logs:/usr/local/tomcat/logs  -v /opt/micro/tomcat/conf/tomcat-users.xml:/usr/local/tomcat/conf/tomcat-users.xml -v /opt/micro/tomcat/conf/context.xml:/usr/local/tomcat/webapps/manager/META-INF/context.xml -d docker.io/tomcat:8
```
* micro-message  > tomcat启动
```
docker run --privileged=true --name micro-message -p 8003:8080 -v /opt/micro/tomcat/logs:/usr/local/tomcat/logs  -v /opt/micro/tomcat/conf/tomcat-users.xml:/usr/local/tomcat/conf/tomcat-users.xml -v /opt/micro/tomcat/conf/context.xml:/usr/local/tomcat/webapps/manager/META-INF/context.xml -d docker.io/tomcat:8
```
* zookeeper > 启动
```
docker run --privileged=true --name zookeeper -p 2181:2181 -v /opt/micro/zookeeper:/data --restart always -d docker.io/zookeeper
```
* dubbo-admin > 启动
```
docker run --privileged=true --name dubbo-admin -d \
-p 9001:8080 \
-e dubbo.registry.address=zookeeper://192.168.109.128:2181 \
-e dubbo.admin.root.password=753951 \
-e dubbo.admin.guest.password=guest \
-v /opt/micro/dubbo-admin/logs:/usr/local/tomcat/logs \
chenchuxin/dubbo-admin
```
* rabbitmq > 启动  

这里注意获取镜像的时候要获取management版本的，不要获取last版本的，management版本的才带有管理界面。  

默认端口：
5672 -- client端通信口
15672 -- 管理界面ui端口
```
docker run  --privileged=true --restart=always -p 5672:5672 -p 15672:15672 --name rabbitmq -d  rabbitmq:management
```
* jenkins > 启动  

默认端口：
8080 -- server页面端口
50000 -- Slave 与 Master 通信端口
```
docker run -d --privileged=true --name jenkins  -u root  -p 9002:8080 -p 50000:50000  -v /opt/micro/jenkins:/var/jenkins_home docker.io/jenkins
```

#### 注意事项
* Linux 相关端口一定要开放，还要注意一些容易忽视的端口号，比如dubbo:20880。
* Linux 端口不能冲突。
* Jenkins 构建异常，除了查看Jekins页面报错信息，还要查看对应项目日志。
* Jenkins git配置-SSH方式：Username 可以通过公钥末尾项获取；Private Key 私钥全部内容。
* Dubbo 提供方需要指定注册中心端口，消费方不需要指定注册中心端口。
* Dubbo-admin 没有数据，多半是注册中心连不上。
* ZK 数据windows可视化工具[ZooInspector]
* 同一宿主机下各个容器间通信：本项目使用默认bridge方式。A容器需要B容器的服务，可是使用[宿主机IP]+[宿主机被映射端口]的方式访问。
* Docker 可视化管理平台推荐使用[Portainer]