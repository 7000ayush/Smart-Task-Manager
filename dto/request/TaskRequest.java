package com.taskmang.dto.request;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class TaskRequest {
    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String description;

    @Size(max = 50)
    private String category;

    @NotNull
    private Date dueDate;

    @NotBlank
    @Size(max = 20)
    private String status;
}
