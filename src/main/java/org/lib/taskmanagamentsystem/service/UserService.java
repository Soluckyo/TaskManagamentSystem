package org.lib.taskmanagamentsystem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.lib.taskmanagamentsystem.entity.Status;
import org.lib.taskmanagamentsystem.entity.Task;
import org.lib.taskmanagamentsystem.entity.User;
import org.lib.taskmanagamentsystem.repository.TaskRepo;
import org.lib.taskmanagamentsystem.repository.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TaskRepo taskRepo;
    private final UserRepo userRepo;

    public Page<Task> getTasksByUser(Long userId, Pageable pageable) {
        userRepo.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id: " + userId + " не найден"));

        return taskRepo.findByUserId(userId, pageable);
    }

    public void changeStatus(Long taskId, User user, Status status) throws AccessDeniedException {
        Task task = taskRepo.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Задача с ID: " + taskId + " не найдена"));
        if (!task.getUser().equals(user)) {
            throw new AccessDeniedException(" Вы не можете редактировать эту задачу");
        }
        task.setStatus(status);
        taskRepo.save(task);
    }

    public void addComment(String comment, User user, Long taskId) throws AccessDeniedException {
        Task task = taskRepo.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Задача с ID: " + taskId + " не найдена"));
        if (!task.getUser().equals(user)) {
            throw new AccessDeniedException(" Вы не можете редактировать эту задачу");
        }
        task.setComment(comment);
        taskRepo.save(task);
    }

}
