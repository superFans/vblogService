package org.sang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        testNativeMethodOutOfMemory();
        return "server被调用了！:" + name;
    }
    //本地方法栈溢出
    public static void testNativeMethodOutOfMemory(){
        int j = 0;
        while(true){
            System.out.println(j++);
            ExecutorService executors = Executors.newFixedThreadPool(50);
            int i=0;
            while(i++<10){
                executors.submit(new Runnable() {
                    public void run() {
                    }
                });
            }
        }
    }
}
