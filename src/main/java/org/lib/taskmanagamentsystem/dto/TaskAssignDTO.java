package org.lib.taskmanagamentsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "DTO с ID пользователя и ID задачи")
public class TaskAssignDTO {

    @Schema(description = "Сущность задачи с исполнителем")
    private Long taskId;

    @Schema(description = "ID исполнителя", example = "21")
    private Long userId;
}
