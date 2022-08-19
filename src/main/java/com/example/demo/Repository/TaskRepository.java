package com.example.demo.Repository;

import com.example.demo.bean.Customer;
import com.example.demo.bean.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task,String> {
    List<Task> findByTaskName(String task_name);
    Task findById(long taskId);
    @Query(value="select t from Task t where (t.taskName=:#{#taskname} or :#{#taskname} is null) and (t.id=:#{#taskid} or :#{#taskid} is null) and (t.date>=:#{#starttime} or :#{#starttime} is null) and (t.date<=:#{#finishedtime} or :#{#finishedtime} is null)")
    List<Task> findWithConditions(@Param("taskname") String taskname, @Param("taskid") Long taskid, @Param("starttime") Long starttime, @Param("finishedtime") Long finishedtime);
    @Transactional
    @Modifying
    Task deleteTaskById(long taskId);
}
