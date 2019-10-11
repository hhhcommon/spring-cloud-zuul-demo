#Integrated spring cloud zuul with apollo
Archaius是Netflix的配置中心客户端(开源)，没有开预案配置服务器端，可以与Apollo结合。<br/>
Zuul的一个功能是路由转发。
## 编写Spring Cloud Zuul
### POM依赖
```xml
 <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>1.8</java.version>
    <spring.cloud.version>2.1.3.RELEASE</spring.cloud.version>
</properties>
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        <version>${spring.cloud.version}</version>
    </dependency>
    <dependency>
        <groupId>com.ctrip.framework.apollo</groupId>
        <artifactId>apollo-client</artifactId>
        <version>1.4.0</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```
### 编写主程序
```java
package com.edu.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class SpringCloudZuulApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudZuulApplication.class, args);
    }
}
```
### 编写Filter
* PreFilter
```java
package com.edu.zuul.filters;

import com.edu.zuul.config.DemoApolloConfig;
import com.edu.zuul.config.DemoGlobalConfig;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class PreFilter extends ZuulFilter {
    @Autowired
    private DemoGlobalConfig demoGlobalConfig;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        System.out.println("Request Method : " + request.getMethod() + " Request URL : " + request.getRequestURL().toString());
        System.out.println("Debug Result : " + demoGlobalConfig.isPreDebug());
        return null;
    }
}
```
* PostFilter
```java
package com.edu.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;

public class PostFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        System.out.println("Post Filter");
        return null;
    }
}
```
* RouteFilter
```java
package com.edu.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;

public class RouteFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        System.out.println("Route Filter");
        return null;
    }
}
```
* ErrorFilter
```java
package com.edu.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;

public class ErrorFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        System.out.println("Error Filter");
        return null;
    }
}
```
### 编写配置类
* SpringCloudZuulConfig
```java
package com.edu.zuul.config;

import com.edu.zuul.filters.ErrorFilter;
import com.edu.zuul.filters.PostFilter;
import com.edu.zuul.filters.PreFilter;
import com.edu.zuul.filters.RouteFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringCloudZuulConfig {
    @Bean
    public PreFilter preFilter() {
        return new PreFilter();
    }

    @Bean
    public PostFilter postFilter() {
        return new PostFilter();
    }

    @Bean
    public ErrorFilter errorFilter() {
        return new ErrorFilter();
    }

    @Bean
    public RouteFilter routeFilter() {
        return new RouteFilter();
    }
}
```
## Apollo配置
### 编写Apollo配置类
```java
package com.edu.zuul.config;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableApolloConfig
public class DemoApolloConfig {
    @Bean
    public DemoGlobalConfig globalConfig() {
        return new DemoGlobalConfig();
    }
}
```
```java
package com.edu.zuul.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class DemoGlobalConfig {
    @Value("${debug.pre.enable}")
    private boolean preDebug;
}
```
### 配置文件
```yaml
zuul:
  routes:
    student:
      url: http://localhost:9700  # 其他服务的路径
server:
  port: 9000 # 当前服务的端口
app:
  id: SampleApp 
apollo:
  bootstrap:
    enabled: true
  meta: https://localhost:8080

debug:
  pre:
    enable: false
```
## 集成实现
### 启动Apollo
```
start config service  for client 
start admin service for portal 8090
start portal service 8070
eureka 8080
```
### 启动程序
```
启动student-service
启动springcloudzuuldemo，添加启动参数:-Dapollo.configService=http://localhost:8080
如果没有添加启动参数,会出现2019-10-10 19:57:06.151  WARN 16383 --- 
[ngPollService-1] c.c.f.a.i.RemoteConfigLongPollService:
Long polling failed, will retry in 8 seconds. appId: SampleApp, cluster: default,
namespaces: application, long polling url: null, reason: Get config services 
failed from https://localhost:8080/services/config?appId=SampleApp&ip=10.2.200.135 
[Cause: Could not complete get operation [Cause: Unrecognized SSL message, 
plaintext connection?]]
```
### 验证参数
#### student-service服务没有启动
```
Request Method : GET Request URL : http://localhost:9000/student/echo/xiaoyao
Debug Result : false
Route Filter
2019-10-10 20:12:19.726  WARN 16624 --- [nio-9000-exec-4] o.s.c.n.z.filters.post.SendErrorFilter   : Error during filtering
com.netflix.zuul.exception.ZuulException: Forwarding error
Error Filter
Post Filter
```
### student-service 启动成功
```
Request Method : GET Request URL : http://localhost:9000/student/echo/xiaoyao
Debug Result : false
Route Filter
Post Filter
```
student-service可以正常返回结果。
### 验证Apollo 
页面修改参数为debug.pre.enable=true
```
# 在页面上修改之后,输出的信息
2019-10-11 10:27:56.917  INFO 704 --- [Apollo-Config-1] c.f.a.s.p.AutoUpdateConfigChangeListener : Auto update apollo 
changed value successfully, new value: true, key: debug.pre.enable, beanName: globalConfig, 
field: com.edu.zuul.config.DemoGlobalConfig.preDebug
Request Method : GET Request URL : http://localhost:9000/student/echo/xiaoyao
Debug Result : true
Route Filter
Post Filter
```
student-service可以正常返回结果。
## 参考代码
[spring-cloud-zuul-demo](https://github.com/yishengxiaoyao/spring-cloud-zuul-demo)
