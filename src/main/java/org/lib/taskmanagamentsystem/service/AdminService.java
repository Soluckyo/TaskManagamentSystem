package org.lib.taskmanagamentsystem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.lib.taskmanagamentsystem.dto.TaskDTO;
import org.lib.taskmanagamentsystem.entity.Task;
import org.lib.taskmanagamentsystem.entity.User;
import org.lib.taskmanagamentsystem.repository.TaskRepo;
import org.lib.taskmanagamentsystem.repository.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TaskRepo taskRepo;

    private final UserRepo userRepo;

    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepo.findAll(pageable);
    }

    public Task findTaskById(Long id) {
        return taskRepo.findById(id).orElse(null);
    }

    public void addTask(Task task) {
        taskRepo.save(task);
    }

    public void updateTask(Long taskId, TaskDTO taskDto) {
        Task taskToUpdate = findTaskById(taskId);
        if (taskToUpdate == null) {
            throw new EntityNotFoundException("Task with id: " + taskId + " not found");
        }

        if (taskDto.getTitle() != null) {
            taskToUpdate.setTitle(taskDto.getTitle());
        }
        if (taskDto.getBody() != null) {
            taskToUpdate.setBody(taskDto.getBody());
        }
        if (taskDto.getStatus() != null) {
            taskToUpdate.setStatus(taskDto.getStatus());
        }
        if (taskDto.getPriority() != null) {
            taskToUpdate.setPriority(taskDto.getPriority());
        }
        if (taskDto.getComment() != null) {
            taskToUpdate.setComment(taskDto.getComment());
        }

        taskRepo.save(taskToUpdate);
    }

    public void deleteTask(Long id) {
        taskRepo.deleteById(id);
    }

    public void assignTaskToUser(Long userId, Long taskId) {
        User user = userRepo.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id: " + userId + " не найден"));

        Task task = taskRepo.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Задача с id: " + taskId + " не найдена"));

        if(task.getUser() != null){
            task.getUser().getTask().remove(task);
        }

        user.getTask().add(task);
        userRepo.save(user);

        task.setUser(user);
        taskRepo.save(task);
    }

    public void addComment(Long taskId, String comment) {
        Optional<Task> task = Optional.ofNullable(findTaskById(taskId));
        if (task.isPresent()) {
            task.get().setComment(comment);
            taskRepo.save(task.get());
        }else throw new EntityNotFoundException("Task with id: " + taskId + " not found");
    }
}
