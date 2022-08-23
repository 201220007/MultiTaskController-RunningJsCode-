package com.example.demo.bean;

import javax.persistence.*;

@Entity
@Table(name = "Executions")
public class Execution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "TaskId")
    private Long taskId=null;
    @Column(name = "TaskName")
    private String taskName=null;
    @Column(name = "TaskContent")
    private String taskContent=null;
    @Column(name = "StartTime")
    private Long startTime=null;
    @Column(name = "RunningTime")
    private Long runningTime=null;
    @Column(name = "Status")
    private String status=null;
    @Column(name = "Result")
    private String result=null;
    @Column(name = "ExpectedTime")
    private Long expectedTime=null;
    @Column(name = "ExpectedMemory")
    private Long expectedMemory=null;
    @Column(name = "Parameter")
    private String parameter=null;
    public Execution() {
    }
    public Execution(long taskId, String taskContent, long startTime,long runningTime,String status,String result,long expectedTime,long expectedMemory,String parameter) {
        this.taskId = taskId;
        this.taskContent = taskContent;
        this.startTime = startTime;
        this.runningTime=runningTime;
        this.status=status;
        this.result=result;
        this.expectedTime=expectedTime;
        this.expectedMemory=expectedMemory;
        this.parameter=parameter;
    }
    public long getId(){return id;}
    public long getTaskId() {
        return taskId;
    }
    public void setTaskId(long taskId){this.taskId=taskId;}
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getTaskContent() {
        return taskContent;
    }
    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }
    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public long getRunningTime() {
        return runningTime;
    }
    public void setRunningTime(long runningTime) {
        this.runningTime = runningTime;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status){this.status=status;}
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public long getExpectedTime() {
        return expectedTime;
    }
    public void setExpectedTime(long expectedTime){this.expectedTime=expectedTime;}
    public long getExpectedMemory() {
        return expectedMemory;
    }
    public void setExpectedMemory(long expectedMemory) {
        this.expectedMemory = expectedMemory;
    }
    public void setParameter(String parameter){ this.parameter=parameter; };
    public String getParameter(){ return parameter; };
    @Override
    public String toString() {
        return "{\"executionId\":\""+id+"\",\"taskId\":\""+taskId+"\",\"taskName\":\""+taskName+"\",\"task_content\":\""+taskContent+"\",\"start_time\":"+startTime+",\"running_time\":"+runningTime+",\"status\":\""+status+"\",\"result\":\""+result+"\",\"parameter\":\""+parameter+"\"}";
    }
}
