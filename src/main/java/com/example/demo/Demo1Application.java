package com.example.demo;

import com.example.demo.Controller.AsyncTaskService;
import com.example.demo.Controller.CommonService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.script.ScriptException;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableJpaRepositories
@ComponentScan(basePackages="com.example.demo.Controller")
public class Demo1Application {

    public static void main(String[] args) throws InterruptedException, ScriptException {
        ConfigurableApplicationContext context = SpringApplication.run(Demo1Application.class, args);
        CommonService commonService=context.getBean(CommonService.class);
        commonService.load_all_processing_executions();//把因为系统意外没执行完的数据都挑出来重新执行
        AsyncTaskService asyncTaskService = context.getBean(AsyncTaskService.class);
        asyncTaskService.save_execution_to_disk();//周期性的检查cache里面的数据是否过多，并删除一部分
        asyncTaskService.save_task_to_disk();//周期性的检查cache里面的数据是否过多，并删除一部分
    }

}
