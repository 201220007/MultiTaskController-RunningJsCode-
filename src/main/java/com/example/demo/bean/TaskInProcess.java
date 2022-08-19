package com.example.demo.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TaskInProcess")
public class TaskInProcess {
    @Id
    private String taskName=null;
    @Column(name = "TaskContent")
    private String taskContent=null;
    @Column(name = "StartTime")
    private long startTime=0;
    @Column(name = "RunningTime")
    private long runningTime=0;
    @Column(name = "Status")
    private String status=null;
    @Column(name = "Result")
    private String result=null;
    @Column(name = "ExpectedTime")
    private long expectedTime=0;
    @Column(name = "ExpectedMomery")
    private long expectedMomery=0;
    public TaskInProcess() {
    }
    public TaskInProcess(String taskName, String taskContent, long startTime,long runningTime,String status,String result,long expectedTime,long expectedMemory) {
        this.taskName = taskName;
        this.taskContent = taskContent;
        this.startTime = startTime;
        this.runningTime=runningTime;
        this.status=status;
        this.result=result;
        this.expectedTime=expectedTime;
        this.expectedMomery=expectedMemory;
    }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName){this.taskName=taskName;}
    public String getTaskContent() {
        return taskContent;
    }
    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }
    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long start_time) {
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
    public long getExpectedMomery() {
        return expectedMomery;
    }
    public void setExpectedMomery(long expectedMomery) {
        this.expectedMomery = expectedMomery;
    }
    @Override
    public String toString() {
        return "{\"taskName\":\""+taskName+"\",\"task_content\":\""+taskContent+"\",\"start_time\":"+startTime+",\"running_time\":"+runningTime+",\"status\":\""+status+"\",\"result\":\""+result+"\"}";
    }
}
