package org.lib.taskmanagamentsystem;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lib.taskmanagamentsystem.dto.TaskDTO;
import org.lib.taskmanagamentsystem.entity.Priority;
import org.lib.taskmanagamentsystem.entity.Role;
import org.lib.taskmanagamentsystem.entity.Status;
import org.lib.taskmanagamentsystem.entity.Task;
import org.lib.taskmanagamentsystem.entity.User;
import org.lib.taskmanagamentsystem.repository.TaskRepo;
import org.lib.taskmanagamentsystem.repository.UserRepo;
import org.lib.taskmanagamentsystem.service.AdminService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private TaskRepo taskRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private AdminService adminService;

    private Task task;
    private Task otherTask;
    private User user;
    private User otherUser;
    private Status status;
    private TaskDTO taskDTO;
    private Pageable pageable;
    private Page<Task> taskPage;
    private List<Task> taskList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        user = new User(
                1L, "John Doe", "Doe@mail.com", "pass", taskList, Role.USER
        );

        otherUser = new User(
                2L, "John Goe", "Goe@mail.com", "pass", taskList, Role.USER
        );

        task = new Task(
                1L, "Title", "Comment","Body", Status.RUNNING, Priority.HIGH, null);

        otherTask = new Task(
                2L, "Title 2", "Comment 2","Body 2", Status.PENDING, Priority.LOW, user
        );
        taskDTO = new TaskDTO(
                "Title 3", "Comment 3", "Body 3", Priority.LOW, Status.PENDING
        );

        status = Status.COMPLETED;

        pageable = PageRequest.of(0, 1);

        List<Task> taskList = List.of(task, otherTask);
        taskPage = new PageImpl<>(taskList);
    }

    @Test
    void getAllTasksSuccessfully() {
        when(taskRepo.findAll(pageable)).thenReturn(taskPage);

        Page<Task> result = adminService.getAllTasks(pageable);

        assertEquals(2, result.getContent().size(), "Количество задач должно быть 2");
        assertEquals("Title", result.getContent().get(0).getTitle());
        assertEquals("Title 2", result.getContent().get(1).getTitle());
    }

    @Test
    void addTaskSuccessfully() {
        when(taskRepo.save(task)).thenReturn(task);
        adminService.addTask(task);
        verify(taskRepo, times(1)).save(task);
    }

    @Test
    void updateTaskSuccessfully() {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        adminService.updateTask(1L, taskDTO);

        assertEquals("Title 3", task.getTitle());
        assertEquals("Comment 3", task.getComment());
        assertEquals("Body 3", task.getBody());
        assertEquals(Priority.LOW, task.getPriority());
        assertEquals(Status.PENDING, task.getStatus());

        verify(taskRepo, times(1)).save(task);
    }

    @Test
    void updateTaskWhenTaskNotFound() {
        when(taskRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> adminService.updateTask(1L, taskDTO));
        verify(taskRepo, never()).save(any());
    }

    @Test
    void deleteTaskSuccessfully() {
        doNothing().when(taskRepo).deleteById(1L);
        adminService.deleteTask(1L);
        verify(taskRepo, times(1)).deleteById(1L);
    }

    @Test
    void assignTaskToUserSuccessfully() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        // Вызовем метод для назначения задачи пользователю
        adminService.assignTaskToUser(1L, 1L);

        // Проверяем, что задача теперь назначена пользователю
        assertEquals(user, task.getUser());
        assertTrue(user.getTask().contains(task));

        // Проверяем, что метод save был вызван для пользователя и задачи
        verify(userRepo, times(1)).save(user);
        verify(taskRepo, times(1)).save(task);
    }

    @Test
    void assignTaskToUserWhenUserNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> adminService.assignTaskToUser(1L, 1L));
        verify(taskRepo, never()).save(any());
        verify(userRepo, never()).save(any());

    }

    @Test
    void assignTaskToUserWhenTaskNotFound() {
        when(taskRepo.findById(1L)).thenReturn(Optional.empty());
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        assertThrows(EntityNotFoundException.class, () -> adminService.assignTaskToUser(1L, 1L));
        verify(userRepo, never()).save(any());
        verify(taskRepo, never()).save(any());

    }

    @Test
    void shouldUnassignTaskFromPreviousUserWhenAssignedToNewUser() {
        otherUser.getTask().add(task);
        task.setUser(otherUser);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        adminService.assignTaskToUser(1L, 1L);

        assertEquals(user, task.getUser());
        assertTrue(user.getTask().contains(task));

        verify(userRepo, times(1)).save(user);
        verify(taskRepo, times(1)).save(task);
    }
}
