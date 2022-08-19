package com.example.demo.Repository;

import com.example.demo.bean.Customer;
import com.example.demo.bean.Execution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExecutionRepository extends JpaRepository<Execution, Long> {
    Execution findById(long executionId);
    List<Execution> findByStatus(String status);
    List<Execution> findByTaskId(Long taskId);
    List<Execution> findByStartTimeGreaterThanAndStartTimeLessThan(Long time1,Long time2);

    /*@Query(value = "select e from Execution e where if(:taskname!=null,e.taskName=:taskname,1=1) and (e.id=:taskid or :taskid=0) and (e.startTime>=:starttime or :starttime=0) and (e.startTime<=:finishedtime or :finishedtime=0)")
    List<Execution> findWithDate(@Param("taskname") String taskname, @Param("taskid") Long taskid, @Param("starttime") Long s1, @Param("finishedtime") Long s2);*/
    @Query(value = "select e from Execution e where (e.taskName=:#{#taskname} or :#{#taskname} is null) and (e.taskId=:#{#taskid} or :#{#taskid} is null) and (e.startTime>=:#{#starttime} or :#{#starttime} is null) and (e.startTime<=:#{#finishedtime} or :#{#finishedtime} is null)")//and (e.startTime>=:starttime or :starttime=0) and (e.startTime<=:finishedtime or :finishedtime=0)
    List<Execution> findWithDate(@Param("taskname") String taskname,@Param("taskid") Long taskid,@Param("starttime") Long starttime,@Param("finishedtime") Long finishedtime);

}
