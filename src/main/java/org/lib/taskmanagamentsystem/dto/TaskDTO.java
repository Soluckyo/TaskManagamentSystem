package org.lib.taskmanagamentsystem.dto;

import lombok.Getter;
import lombok.Setter;
import org.lib.taskmanagamentsystem.entity.Priority;
import org.lib.taskmanagamentsystem.entity.Status;

@Getter
@Setter
public class TaskDTO {

    private String title;
    private String comment;
    private String body;
    private Priority priority;
    private Status status;
}
