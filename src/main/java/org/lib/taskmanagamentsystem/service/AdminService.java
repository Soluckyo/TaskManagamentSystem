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

    public void addTask(TaskDTO taskDto) {
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setBody(taskDto.getBody());
        task.setStatus(taskDto.getStatus());
        task.setPriority(taskDto.getPriority());
        task.setComment(taskDto.getComment());
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

    public void assignTaskToUser(User user, Task task) {
        if(user == null || task == null) {
            throw new EntityNotFoundException
                    ("Задача или Пользователь не могут быть null");
        }

        if(task.getUser() != null){
            task.setUser(null);
        }

        user.getTask().add(task);
        userRepo.save(user);

        task.setUser(user);
        taskRepo.save(task);
    }

    public User findUserById(Long userId) {
        return userRepo.findById(userId).orElse(null);
    }

    public void addComment(Long taskId, String comment) {
        Optional<Task> task = Optional.ofNullable(findTaskById(taskId));
        if (task.isPresent()) {
            task.get().setComment(comment);
            taskRepo.save(task.get());
        }else throw new EntityNotFoundException("Task with id: " + taskId + " not found");
    }
}
