package com.example.demo.Controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.Repository.ExecutionRepository;
import com.example.demo.Repository.TaskRepository;
import com.example.demo.bean.*;
import com.example.demo.function.OshiControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Repository.CustomerRepository;

import javax.script.ScriptException;
import java.util.*;
import java.util.concurrent.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Properties;


@RestController
@EnableAsync
@EnableScheduling
public class TestController {
    @Autowired
    AsyncTaskService asyncTaskService;
    @Autowired
    CommonService commonService;

    public static tasks_operation operate2=new tasks_operation();
    public static Map<Long, Execution> dics=new HashMap<>();
    public static Map<Long,Future<String>> dics2=new HashMap<>();
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ExecutionRepository executionRepository;

    /*@GetMapping(value = "/test")
    public @ResponseBody String test(@RequestBody String jsData){

    }*/
    @RequestMapping(value = "/killExecution/{executionId}")
    public @ResponseBody String killExecution(@PathVariable("executionId") long executionId){
        if(commonService.killexecution(executionId)){
            return "{\"status\":success}";
        }else {
            return "{\"status\":fail}";
        }
    }
    @GetMapping(value = "/filterTask")
    public @ResponseBody String filterTask(@RequestBody Query query){
        //judge whether the input of date is legal
        if((query.endTime==-1&&query.startTime!=-1)||query.endTime!=-1&&query.startTime==-1){
            return "{\"state\":\"error\"}";
        }else if (query.endTime > query.startTime){
            return "{\"state\":\"error\"}";
        }
        //convert date to milliseconds(stored in temp[0],temp[1])
        List<Long> temp=new ArrayList<>();
        if(query.startTime!=-1&&query.endTime!=-1) {
            temp = commonService.timeConvert(query.startTime, query.endTime);
        }
        //judge whether there are parameters.
        //The initial value of taskName is "" and others are -1
        //If the String is "" or Long is -1.Convert it to null.
        Long s1=null,s2=null;
        if(query.startTime==-1&&query.endTime==-1){
            s1=null;
            s2=null;
        }else{
            s1= temp.get(0);
            s2=temp.get(1);
        }
        Long id=null;
        if(query.taskId==-1) {
            id=null;
        }else{
            id= query.taskId;
        }
        String name=null;
        if(Objects.equals(query.taskName, "")){
            name=null;
        }else{
            name=query.taskName;
        }
        //now we have the final result:name,id,s1,s2
        List<Task> list=taskRepository.findWithConditions(name,id,s1,s2);
        if(Objects.equals(query.sortByStartTime, "ASC")){
            Collections.sort(list,new TaskComparator());
        }else if(Objects.equals(query.sortByStartTime, "DESC")){
            Collections.sort(list,new TaskComparator());
            Collections.reverse(list);
        }
        return "{"+"\"content\""+":"+list.toString()+"}";
    }

    @GetMapping(value = "/SystemInfo")
    public @ResponseBody JSONObject getSysInfo() throws UnknownHostException {
        return OshiControl.getInfo();
    }

    @GetMapping(value="/filterExecution")
    public @ResponseBody String filterExecution(@RequestBody Query query){
        if((query.endTime==-1&&query.startTime!=-1)||query.endTime!=-1&&query.startTime==-1){
            return "{\"state\":\"error\"}";
        }else if (query.endTime > query.startTime){
            return "{\"state\":\"error\"}";
        }
        if(!Objects.equals(query.sortByRunningTime, "")&& !Objects.equals(query.sortByStartTime, "")){
            return "{\"state\":\"error\"}";
        }
        List<Long> temp=new ArrayList<>();
        if(query.startTime!=-1&&query.endTime!=-1) {
            temp = commonService.timeConvert(query.startTime, query.endTime);
        }
        Long s1=null,s2=null;
        if(query.startTime==-1&&query.endTime==-1){
            s1=null;
            s2=null;
        }else{
            s1= temp.get(0);
            s2=temp.get(1);
        }
        Long id=null;
        if(query.taskId==-1) {
            id=null;
        }else{
            id= query.taskId;
        }
        String name=null;
        if(Objects.equals(query.taskName, "")){
            name=null;
        }else{
            name=query.taskName;
        }
        List<Execution> list=executionRepository.findWithDate(name,id,s1,s2);
        if(Objects.equals(query.sortByRunningTime, "ASC")){
            Collections.sort(list,new ExecutionComparator());
        }else if(Objects.equals(query.sortByRunningTime, "DESC")){
            Collections.sort(list,new ExecutionComparator());
            Collections.reverse(list);
        }
        if(Objects.equals(query.sortByStartTime, "ASC")){
            Collections.sort(list,new ExecutionComparator2());
        }else if(Objects.equals(query.sortByStartTime, "DESC")){
            Collections.sort(list,new ExecutionComparator2());
            Collections.reverse(list);
        }
        return "{"+"\"content\""+":"+list.toString()+"}";
    }
    @PostMapping(value="/deleteTask")
    public @ResponseBody String deleteTask(@RequestParam("taskId") long taskId){
        Task task=taskRepository.findById(taskId);
        if(task==null){
            return "{\"status\":\"fail\"}";
        }
        task.setFlag(false);
        taskRepository.save(task);
        return "{\"status\":\"success\"}";
    }

    @PostMapping(value="/editTask")//同时要修改execution里面的信息
    public @ResponseBody String editTask(@RequestBody String json_data,@RequestParam long taskId){
        Task task=JSON.parseObject(json_data,Task.class);
        Task task_to_be_edited=taskRepository.findById(taskId);
        if(task_to_be_edited!=null){
            if(!task_to_be_edited.getFlag()){
                return "{\"status\":\"fail\"}";
            }
            if(task.getTaskName()!=null){
                task_to_be_edited.setTaskName(task.getTaskName());
            }
            if(task.getTaskContent()!=null){
                task_to_be_edited.setTask_content(task.getTaskContent());
            }
            taskRepository.save(task_to_be_edited);
            return task_to_be_edited.toString();
        }else{
            return "{\"status\":\"fail\"}";
        }
    }

    @PostMapping(value="/addTaskToDatabase")
    public @ResponseBody String test02(@RequestBody String json_data){
        Task task =JSON.parseObject(json_data,Task.class);
        taskRepository.save(task);
        return task.toString();
    }

    @PostMapping("/addTask")
    public @ResponseBody String m(@RequestBody Task task){
        Long totalMilisSeconds = System.currentTimeMillis();
        task.setDate(totalMilisSeconds);
        task=taskRepository.save(task);
        return task.toString();
    }

    @PostMapping("/addExecution")
    public @ResponseBody String d(@RequestBody Execution execution ) throws InterruptedException, ExecutionException, ScriptException {
        if(execution.getExpectedTime()==0 || execution.getExpectedMemory()==0){
            return "{\"operation_status\":\"wrongRunningTimeOrMemory\"}";
        }
        long s = System.currentTimeMillis();
        Task task=taskRepository.findById(execution.getTaskId());
        if(task==null)
        {
            return "{\"operation_status\":\"wrongTaskId\"}";
        } else {
            if(!task.getFlag()){
                return "{\"operation_status\":\"wrongTaskId\"}";
            }
            execution.setTaskName(task.getTaskName());
            execution.setTaskContent(task.getTaskContent());
            return commonService.executeTask(execution);
        }
    }

    @RequestMapping(value="/taskStatus/{taskId}")
    public @ResponseBody String l(@PathVariable("taskId") Long taskId){
        Task task=taskRepository.findById(taskId);
        if(task==null){
            return "{\"content\":id_does_not_existed,\"operation_status\":\"error\"}";
        }else{
            if(!task.getFlag()){
                return "{\"operation_status\":\"wrongTaskId\"}";
            }
            return task.toString();
        }
    }

    @RequestMapping(value="/executionStatus/{executionId}")
    public @ResponseBody String c(@PathVariable("executionId") Long executionId) throws InterruptedException, ExecutionException, ScriptException {
        if(!dics.containsKey(executionId)) {
            if(Objects.equals(commonService.get_execution_to_cache(executionId), "wrongExecutionId")) {
                return "{\"content\":id_does_not_existed,\"operation_status\":\"error\"}";
            }
        }
        Execution temp1=new Execution();
        if(dics.containsKey(executionId)) {
            if(dics2.containsKey(executionId)) {
                if (dics2.get(executionId).isDone()) {

                } else if (!dics2.get(executionId).isDone()) {
                    dics.get(executionId).setStatus("process");
                    long s2 = System.currentTimeMillis();
                    dics.get(executionId).setRunningTime(s2 - dics.get(executionId).getStartTime());
                }
                temp1 = dics.get(executionId);
            }else{
                temp1=dics.get(executionId);
            }
        }
        return temp1.toString();
    }
    public static String map2JSONStr(Map m) {
        String str_start = "{";
        String str_end = "}";
        String str_content = "";
        int int_now = 0;

        for(Object k: m.keySet()){
            int_now ++;
            if (int_now != m.size()) {
                str_content += "\"" + k.toString() + "\": \"" + m.get(k).toString() + "\", ";
            } else {
                str_content += "\"" + k.toString() + "\": \"" + m.get(k).toString() + "\"";
            }
        }

        return str_start + str_content + str_end;
    }
}

class ExecutionComparator implements Comparator<Execution>{
    @Override
    public int compare(Execution e1, Execution e2){
       if(e1.getRunningTime()>e2.getRunningTime()){
           return 1;
       }else{
           return -1;
       }
    }
}
class ExecutionComparator2 implements Comparator<Execution>{
    @Override
    public int compare(Execution e1, Execution e2){
        if(e1.getStartTime()>e2.getStartTime()){
            return 1;
        }else{
            return -1;
        }
    }
}

class TaskComparator implements Comparator<Task>{
    @Override
    public int compare(Task t1, Task t2){
        if(t1.getDate()>t2.getDate()){
            return 1;
        }else{
            return -1;
        }
    }
}

