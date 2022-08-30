package com.example.demo.Controller;

import com.example.demo.Controller.AsyncTaskService;
import com.example.demo.Repository.ExecutionRepository;
import com.example.demo.Repository.TaskInProcessRepository;
import com.example.demo.Repository.TaskRepository;
import com.example.demo.bean.Execution;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    @Autowired
    private JdbcTemplate jdbcTemplate;
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
        Future<String> future1;
        if(!execution.getIsUrgent()) {
            future1 = asyncTaskService.exec2(execution.getId(), content, execution.getExpectedTime(), execution.getExpectedMemory());
        }else{
            future1 = asyncTaskService.exec3(execution.getId(), content, execution.getExpectedTime(), execution.getExpectedMemory());
        }
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

    public void batchInsert(List<Execution> list){
        System.out.println(1);
        String sql = "insert into executions(id,taskId,taskName,taskContent,startTime,runningTime,status,result,expectedTime,expectedMemory,parameter) values(?,?,?,?,?,?,?,?,?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setLong(1, list.get(i).getId());
                preparedStatement.setLong(2, list.get(i).getTaskId());
                preparedStatement.setString(3, list.get(i).getTaskName());
                preparedStatement.setString(4, list.get(i).getTaskContent());
                preparedStatement.setLong(5, list.get(i).getStartTime());
                preparedStatement.setLong(6, list.get(i).getRunningTime());
                preparedStatement.setString(7, list.get(i).getStatus());
                preparedStatement.setString(8, list.get(i).getResult());
                preparedStatement.setLong(9, list.get(i).getExpectedTime());
                preparedStatement.setLong(10, list.get(i).getExpectedMemory());
                preparedStatement.setString(11, list.get(i).getParameter());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }
}
