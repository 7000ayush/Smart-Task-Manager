package com.taskmang.service;

import java.util.List;

import com.taskmang.dto.request.TaskRequest;
import com.taskmang.dto.response.TaskResponse;

public interface TaskService {
	List<TaskResponse> findAllByUser(String username);
	TaskResponse findById(Long taskId, String username);
	TaskResponse create(String username, TaskRequest taskRequest);
	TaskResponse update(Long taskId, String username, TaskRequest taskRequest);
	void delete(Long taskId, String username);
	byte[] exportTasks(String username, String format);
	List<TaskResponse> findTasksDueToday(String username);
	List<TaskResponse> findUpcomingTasks(String username);
	Object getTaskStats(String username);
}
