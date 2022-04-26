
#添加依赖的镜像
FROM openjdk:8-jdk-alpine

#linux挂载点为/tmp,容器内此路径会对应宿主机的某个文件夹
VOLUME /home/logs

#将target目录下的jar包添加到容器,第一个是系统jar,第二个是别名
ADD  ./target/blogserver-0.0.1-SNAPSHOT.jar vblog.jar

#配置环境变量
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo '$TZ' > /etc/timezone

#设置编码
ENV LANG C.UTF-8

#配置容器，使其可执行化
ENTRYPOINT [ "java","-Djava.security.egd=file:/dev/./urandom", "-jar", "/vblog.jar" ]
#ENTRYPOINT [ "sh", "-c", "exec java -jar /user.jar --spring.profiles.active=test"]

#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5008","-Dspring.profiles.active=test","/user.jar"]

#项目端口
EXPOSE 2001 2001