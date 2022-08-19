package com.example.demo.Controller;

import com.example.demo.Repository.ExecutionRepository;
import com.example.demo.Repository.TaskRepository;
import com.example.demo.bean.Execution;
import com.example.demo.bean.tasks_operation;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

import static com.example.demo.Controller.TestController.dics;
import static com.example.demo.Controller.TestController.dics2;

@Service
public class AsyncTaskService {
    static tasks_operation operate=new tasks_operation();
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ExecutionRepository executionRepository;

    @Async("taskExecutor")
    public void save_execution_to_disk() throws InterruptedException {
        int k=0;
        while(true) {
            Thread.sleep(60000);
            k=k+1;
            List<Long> deleteList=new ArrayList<>();
            System.out.println(k+" round");
            for (Map.Entry<Long,Execution> entry : dics.entrySet()) {
                System.out.println(entry.getKey());
                if (Objects.equals(entry.getValue().getStatus(), "finished")||Objects.equals(entry.getValue().getStatus(), "JS_error")||Objects.equals(entry.getValue().getStatus(), "time_exceeded")||Objects.equals(entry.getValue().getStatus(), "killed")) {
                    System.out.println(entry.getKey());
                    executionRepository.save(entry.getValue());
                    System.out.println("start remove");
                    deleteList.add(entry.getKey());
                    System.out.println("finish remove");
                }
            }
            for (Long i:deleteList){
                dics2.remove(i);
                dics.remove(i);
            }
        }
    }
    @Async("taskExecutor")
    public Future<String> exec2(Long executionId,String program,long expectedTime,long expectedMemory) throws InterruptedException, ScriptException {
        NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.setMaxCPUTime(expectedTime);// 设置脚本执行允许的最大CPU时间（以毫秒为单位），超过则会报异常,防止死循环脚本
        sandbox.setMaxMemory(expectedMemory); //设置JS执行程序线程可以分配的最大内存（以字节为单位），超过会报ScriptMemoryAbuseException错误
        sandbox.allowNoBraces(false); // 是否允许使用大括号
        sandbox.allowLoadFunctions(true); // 是否允许nashorn加载全局函数
        sandbox.setMaxPreparedStatements(30); // because preparing scripts for execution is expensive // LRU初缓存的初始化大小，默认为0
        sandbox.setExecutor(Executors.newFixedThreadPool(4));// 指定执行程序服务，该服务用于在CPU时间运行脚本
        String result = "null";
        try {
            result= sandbox.eval(program).toString();
            operate.set_status_and_runningtime(executionId,"finished");
            dics.get(executionId).setResult(result);
        }catch (ScriptCPUAbuseException e) {
            System.out.println("time exceeded");
            operate.set_status_and_runningtime(executionId,"time_exceeded");
        }catch (ScriptException e2) {
            System.out.println("JS error");
            operate.set_status_and_runningtime(executionId,"JS_error");
        }
        System.out.println(dics.get(executionId).getStatus());
        System.out.println(result);
        executionRepository.save(dics.get(executionId)); //当execution状态发生改变的时候，立马更新到数据库里
        return new AsyncResult<String>(result);
    }

}
