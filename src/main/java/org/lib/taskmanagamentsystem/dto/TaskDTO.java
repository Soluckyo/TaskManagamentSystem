package org.lib.taskmanagamentsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.lib.taskmanagamentsystem.entity.Priority;
import org.lib.taskmanagamentsystem.entity.Status;

@Getter
@Setter
@Schema(description = "сущность задачи")
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    @Schema(description = "Заголовок задачи", example = "Задача №4")
    private String title;

    @Schema(description = "Комментарий к задаче", example = "Приступил к выполнению")
    private String comment;

    @Schema(description = "Тело задачи", example = "Сделать тестовое задание")
    private String body;

    @Schema(description = "Приоритет задачи")
    private Priority priority;

    @Schema(description = "Статус задачи")
    private Status status;
}
