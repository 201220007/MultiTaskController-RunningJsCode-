package com.example.demo.Controller;

import com.example.demo.Repository.ExecutionRepository;
import com.example.demo.Repository.TaskRepository;
import com.example.demo.bean.Execution;
import com.example.demo.bean.Task;
import com.example.demo.bean.tasks_operation;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.util.*;
import java.util.concurrent.*;

import static com.example.demo.Controller.TestController.*;

@Slf4j
@Service
public class AsyncTaskService {
    static tasks_operation operate=new tasks_operation();
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ExecutionRepository executionRepository;

    @Async(value="primaryExecutor")
    public void save_execution_to_disk() throws InterruptedException {
        int k=0;
        Random r=new Random();
        deleteParameter=3;
        while(true) {
            Thread.sleep(60000);
            k = k + 1;
            List<Long> deleteList = new ArrayList<>();
            System.out.println(k + " round_execution");
            System.out.println(dics.size());
            int start = 1;
            if (dics.size() > CacheSize) {
                int i = r.nextInt(deleteParameter) + 1;//生成1—3之间的随机数
                for (Map.Entry<Long, Execution> entry : dics.entrySet()) {
                    if ((start == i) && Objects.equals(entry.getValue().getStatus(), "finished") || Objects.equals(entry.getValue().getStatus(), "JS_error") || Objects.equals(entry.getValue().getStatus(), "time_exceeded") || Objects.equals(entry.getValue().getStatus(), "killed")) {
                        deleteList.add(entry.getKey());
                        i = i + r.nextInt(deleteParameter) + 1;//每隔1-3轮从cache里删一条数据
                    }
                    start++;
                }
                for (Long m : deleteList) {
                    dics2.remove(m);
                    dics.remove(m);
                }
            }
        }
    }
    @Async(value="primaryExecutor")
    public void save_task_to_disk() throws InterruptedException {
        int k=0;
        Random r=new Random();
        deleteParameter=3;
        while(true) {
            Thread.sleep(60000);
            k = k + 1;
            List<Long> deleteList = new ArrayList<>();
            System.out.println(k + " round_task");
            System.out.println(dics3.size());
            int start = 1;
            if (dics3.size() > TaskCacheSize) {
                int i = r.nextInt(deleteParameter) + 1;//生成1—3之间的随机数
                for (Map.Entry<Long, Task> entry : dics3.entrySet()) {
                    if ((start == i)||!entry.getValue().getFlag()) {
                        deleteList.add(entry.getKey());
                        i = i + r.nextInt(deleteParameter) + 1;//每隔1-3轮从cache里删一条数据
                    }
                    start++;
                }
                for (Long m : deleteList) {
                    dics3.remove(m);
                }
            }
        }
    }
    @Async(value="primaryExecutor")
    public Future<String> exec2(Long executionId,String program,long expectedTime,long expectedMemory) throws InterruptedException, ScriptException {
        System.out.println(Thread.currentThread().getName());
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
    @Async(value="secondaryExecutor")
    public Future<String> exec3(Long executionId,String program,long expectedTime,long expectedMemory) throws InterruptedException, ScriptException {
        System.out.println(Thread.currentThread().getName());
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
