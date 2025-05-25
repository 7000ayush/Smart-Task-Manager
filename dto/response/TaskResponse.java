package com.taskmang.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Date dueDate;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
