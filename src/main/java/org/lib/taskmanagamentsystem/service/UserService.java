package org.lib.taskmanagamentsystem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.lib.taskmanagamentsystem.entity.Status;
import org.lib.taskmanagamentsystem.entity.Task;
import org.lib.taskmanagamentsystem.entity.User;
import org.lib.taskmanagamentsystem.repository.TaskRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TaskRepo taskRepo;

    public ResponseEntity<String> changeStatus(Status status, User user, Long taskId) {
        Task task = taskRepo.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Задача с ID: " + taskId + " не найдена"));
        if(task.getStatus().equals(status)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Значение статуса не было изменено");
        }
        if(!task.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Вы не можете изменять эту задачу. Вы не являетесь исполнителем");
        }
        task.setStatus(status);
        taskRepo.save(task);
        return ResponseEntity.ok("Статус задачи успешно изменен");
    }

    public ResponseEntity<String> addComment(String comment, User user, Long taskId) {
        Task task = taskRepo.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Задача с ID: " + taskId + " не найдена"));
        if(comment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Комментарий не может быть пустым");
        }
        if(!task.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Вы не можете добавлять комментарии. Вы не являетесь исполнителем");
        }
        task.setComment(comment);
        taskRepo.save(task);
        return ResponseEntity.ok("Комментарий успешно добавлен");
    }

}
