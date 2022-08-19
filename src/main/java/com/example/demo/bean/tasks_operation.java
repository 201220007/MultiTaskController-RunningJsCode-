package com.example.demo.bean;

import static com.example.demo.Controller.TestController.dics;

public class tasks_operation {
    public Boolean containtask(String task_name){
        if(dics.containsKey(task_name))
        {
            return true;
        }else {
            return false;
        }
    }
    public String set_status_and_runningtime(Long executionId,String status){
        if(dics.containsKey(executionId))
        {
            dics.get(executionId).setStatus(status);
            long s2 = System.currentTimeMillis();
            dics.get(executionId).setRunningTime(s2-dics.get(executionId).getStartTime());
            return "success";
        } else {
            return "task_name_error";
        }
    }
}
