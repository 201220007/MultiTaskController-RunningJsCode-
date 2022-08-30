package com.example.demo.bean;
import javax.persistence.*;

@Entity
@Table(name = "Tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "TaskName")
    private String taskName=null;
    @Column(name = "TaskContent")
    private String taskContent=null;

    @Column(name = "Date")
    private Long date=null;

    @Column(name="Flag")
    private Boolean flag=true;
    public Task() {
    }
    public Task(String taskName, String taskContent) {
        this.taskName = taskName;
        this.taskContent = taskContent;
    }
    public long getId(){return id;}
    public void setId(long taskId){ this.id=taskId; }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName){this.taskName=taskName;}
    public String getTaskContent() {
        return taskContent;
    }
    public void setTask_content(String taskContent) { this.taskContent = taskContent; }

    public Long getDate(){ return date; }
    public void setDate(Long date){ this.date=date; }
    public Boolean getFlag(){ return flag; }
    public void setFlag(Boolean flag){ this.flag=flag; }
    @Override
    public String toString() {
        return "{\"taskId\":"+id+",\"taskName\":\""+taskName+"\",\"taskContent\":\""+taskContent+"\",\"date\":"+date+"}";
    }
}
