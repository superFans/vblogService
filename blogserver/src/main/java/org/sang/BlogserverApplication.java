package org.sang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@SpringBootApplication
@EnableScheduling//开启定时任务支持
public class BlogserverApplication {
    private final static Logger logger = LoggerFactory.getLogger( BlogserverApplication.class );
    public static void main(String[] args) {
        SpringApplication.run(BlogserverApplication.class, args);
    }



    @GetMapping("/{name}")
    public String hi(@PathVariable(value = "name") String name) {
        logger.info( "name = {}" , name );
        logger.info("你好啊e");
        logger.warn("This is a warn message!");
        logger.error("This is error message!");
        return "server被调用了！:" + name;
    }
}
