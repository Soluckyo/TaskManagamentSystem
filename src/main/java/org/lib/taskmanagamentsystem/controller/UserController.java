package org.lib.taskmanagamentsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.lib.taskmanagamentsystem.entity.Status;
import org.lib.taskmanagamentsystem.entity.Task;
import org.lib.taskmanagamentsystem.entity.User;
import org.lib.taskmanagamentsystem.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;


@Tag(name = "User_controller")
@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @Operation(
            summary = "Получение всех задач конкретного пользователя",
            description = "Принимает Id пользователя и отдает с пагинацией все задачи," +
                    " у которых этот пользователь является исполнителем)"

    )
    @GetMapping("tasks/{userId}")
    public ResponseEntity<Page<Task>> getTaskByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasksPage = userService.getTasksByUser(userId, pageable);
        if (tasksPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(tasksPage);
    }


    @Operation(
            summary = "Изменение статуса задачи",
            description = "Принимает taskId из адреса и новый status, после обновляет поле задачи на новое значение"
    )
    @PutMapping("/change_status/{taskId}")
    public ResponseEntity<String> changeStatus(@RequestBody Status status,
                                               @AuthenticationPrincipal User user,
                                               @PathVariable Long taskId) {
        try{
            userService.changeStatus(taskId, user, status);
            return ResponseEntity.status(HttpStatus.OK).body("Статус задачи успешно обновлен");
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Задача не найдена: " + e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Нет доступа для изменения статуса задачи");
        }
    }


    @Operation(
            summary = "Добавление комментария к задаче",
            description = "Принимает taskId из адреса и comment, после обновляет поле задачи на новое значение"
    )
    @PutMapping("/add_comment/{taskId}")
    public ResponseEntity<String> addComment(@RequestBody String comment,
                                             @AuthenticationPrincipal User user,
                                             @PathVariable Long taskId) {
        try {
            userService.addComment(comment, user, taskId);
            return ResponseEntity.status(HttpStatus.OK).body("Комментарий успешно добавлен");
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Задача не найдена: " + e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Нет доступа для изменения статуса задачи");
        }
    }
}
