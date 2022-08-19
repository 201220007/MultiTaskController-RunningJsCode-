package com.example.demo.Controller;

import com.example.demo.Controller.AsyncTaskService;
import com.example.demo.Repository.ExecutionRepository;
import com.example.demo.Repository.TaskInProcessRepository;
import com.example.demo.Repository.TaskRepository;
import com.example.demo.bean.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static com.example.demo.Controller.TestController.*;

@Service
public class CommonService {
    @Autowired
    AsyncTaskService asyncTaskService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ExecutionRepository executionRepository;
    @Autowired
    private TaskInProcessRepository taskInProcessRepository;
    public String get_execution_to_cache(long executionId) throws InterruptedException, ScriptException {
        Execution temp=executionRepository.findById(executionId);
        if(temp==null) {
            return "wrongExecutionId";
        }
        if(dics.containsKey(temp.getId())) {
            return "Key is already in cache";
        }else {
            dics.put(temp.getId(),temp);
            return "success";
        }
    }
    public List<Long> timeConvert(Long startTime,Long endTime){
        List<Long> list=new ArrayList<>();
        Long totalMilisSeconds = System.currentTimeMillis();
        //总秒数
        Long totalSeconds = totalMilisSeconds / 1000;
        //当前秒数
        Long currentSeconds = totalSeconds % 60;
        //总分钟
        Long totalMinutes = totalSeconds / 60;
        //当前分钟
        Long currentMinutes = totalMinutes % 60;
        //总小时（中国时区需加8小时）
        Long totalHours = totalMinutes / 60 + 8;
        //当前小时
        Long currentHours = totalHours % 24;
        //总天数
        Long totalDays = totalHours / 24;
        Long days=null;
        Long s=null;
        Long s2=null;
        Long days2=null;
        if(startTime!=null&&endTime!=null) {
            days =startTime;
            days2=endTime;
            s = totalMilisSeconds - ((days > 0 ? days : 0) * 1000 * 60 * 60 * 24 + currentSeconds * 1000 + currentMinutes * 1000 * 60 + currentHours * 1000 * 60 * 60);
            s2 = totalMilisSeconds - (((days2 > 0 ? days2 - 1 : 0)) * 1000 * 60 * 60 * 24 + currentSeconds * 1000 + currentMinutes * 1000 * 60 + currentHours * 1000 * 60 * 60);
            if(days2==0)
            {
                s2=totalMilisSeconds;
            }
        }
        list.add(s);
        list.add(s2);
        return list;
    }
    public void load_all_processing_executions() throws ScriptException, InterruptedException {
        List<Execution> temp=executionRepository.findByStatus("process");
        for(Execution t:temp)
        {
            executeTask(t);
        }
    }

    public String executeTask(Execution execution) throws ScriptException, InterruptedException {
        long s = System.currentTimeMillis();
        System.out.println(s);
        execution.setStartTime(s);
        execution.setStatus("process");
        execution = executionRepository.save(execution);
        dics.put(execution.getId(),execution);
        String content=execution.getParameter()+execution.getTaskContent();
        Future<String> future1 = asyncTaskService.exec2(execution.getId(),content,execution.getExpectedTime(),execution.getExpectedMemory());
        dics2.put(execution.getId(),future1);
        return execution.toString();
    }

    public Boolean killexecution(long executionId){
        if(dics2.containsKey(executionId)) {
            operate2.set_status_and_runningtime(executionId,"killed");
            executionRepository.save(dics.get(executionId));
            return dics2.get(executionId).cancel(true);
        }else{
            return false;
        }
    }
}
