package com.taskmang.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import com.taskmang.entity.Task;
import com.taskmang.entity.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
	List<Task> findByUser(User user, Sort sort);
	List<Task> findByUserAndDueDateBetween(User user, Date startDate, Date endDate, Sort sort);
	List<Task> findByUserAndDueDateAfter(User user, Date date, Sort sort);
}
