package org.lib.taskmanagamentsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.lib.taskmanagamentsystem.dto.TaskAssignDTO;
import org.lib.taskmanagamentsystem.dto.TaskDTO;
import org.lib.taskmanagamentsystem.entity.Task;
import org.lib.taskmanagamentsystem.entity.User;
import org.lib.taskmanagamentsystem.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Tag(name = "Admin_controller")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "Получение всех задач",
            description = "Получает из базы все задачи и отдает в виде List<Task>, ничего не принимает"
    )
    @GetMapping("/")
    public ResponseEntity<Page<Task>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasksPage = adminService.getAllTasks(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(tasksPage);
    }


    @Operation(
            summary = "Добавление новой задачи",
            description = "Принимает сущность TaskDTO и сохраняет в базе"
    )
    @PostMapping("/add_task")
    public ResponseEntity<String> addTask(@RequestBody TaskDTO taskDto) {
        adminService.addTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Задача успешно добавлена");
    }


    @Operation(
            summary = "Обновление задачи",
            description = "Принимает taskId и сущность TaskDTO, обновляет поля и сохраняет в базе"
    )
    @PutMapping("/update/{taskId}")
    public ResponseEntity<String> updateTask(@PathVariable Long taskId, @RequestBody TaskDTO taskDto) {

        Task task = adminService.findTaskById(taskId);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Задача не найдена");
        }

        adminService.updateTask(taskId, taskDto);
        return ResponseEntity.status(HttpStatus.OK).body("Задача успешно обновлена");
    }


    @Operation(
            summary = "Удаление задачи",
            description = "Удаляет задачу по Id"
    )
    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        adminService.deleteTask(taskId);
        return ResponseEntity.status(HttpStatus.OK).body("Задача успешно удалена");
    }


    @Operation(
            summary = "Назначение исполнителя задачи",
            description = "Принимает TaskAssignDTO и назначает пользователя исполнителем задачи "
    )
    @PutMapping("/assign")
    public ResponseEntity<String> assignTaskToUser(@RequestBody TaskAssignDTO taskAssignDTO) {
        Optional<Task> taskOpt = Optional.ofNullable(adminService.findTaskById(taskAssignDTO.getTaskId()));
        if (taskOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Задача не найдена");
        }

        Optional<User> userOpt = Optional.ofNullable(adminService.findUserById(taskAssignDTO.getUserId()));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }

        adminService.assignTaskToUser(userOpt.get(), taskOpt.get());
        return ResponseEntity.status(HttpStatus.OK).body("Исполнитель задачи успешно назначен");
    }


    @Operation(
            summary = "Добавление комментария",
            description = "Принимает taskId из адреса и comment из тела запроса и добавляет комментарий к задаче"
    )
    @PutMapping("/add_comment/{taskId}")
    public ResponseEntity<String> addComment(@PathVariable Long taskId,@RequestBody String comment) {
        try {
            adminService.addComment(taskId, comment);
            return ResponseEntity.status(HttpStatus.OK).body("Комментарий успешно добавлен");
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
