package com.example.demo.Repository;

import com.example.demo.bean.Customer;
import com.example.demo.bean.Task;
import com.example.demo.bean.TaskInProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskInProcessRepository extends JpaRepository<TaskInProcess,String> {
    Task findByTaskName(String taskName);
}
