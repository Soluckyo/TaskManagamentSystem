package org.lib.taskmanagamentsystem;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lib.taskmanagamentsystem.entity.Priority;
import org.lib.taskmanagamentsystem.entity.Role;
import org.lib.taskmanagamentsystem.entity.Status;
import org.lib.taskmanagamentsystem.entity.Task;
import org.lib.taskmanagamentsystem.entity.User;
import org.lib.taskmanagamentsystem.repository.TaskRepo;
import org.lib.taskmanagamentsystem.repository.UserRepo;
import org.lib.taskmanagamentsystem.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private TaskRepo taskRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    private Task task;
    private Task otherTask;
    private User user;
    private User otherUser;
    private Status status;
    private Pageable pageable;
    private Page<Task> taskPage;

    @BeforeEach
    void setUp() {
        user = new User(
                1L, "John Doe", "Doe@mail.com", "pass", null, Role.USER
        );

        otherUser = new User(
                2L, "John Goe", "Goe@mail.com", "pass", null, Role.USER
        );

        task = new Task(
                1L, "Title", "Comment","Body", Status.RUNNING, Priority.HIGH, user
        );

        otherTask = new Task(
                2L, "Title 2", "Comment 2","Body 2", Status.PENDING, Priority.LOW, user
        );

        status = Status.COMPLETED;
        List<Task> taskList = List.of(task, otherTask);
        pageable = PageRequest.of(0, 2);
        taskPage = new PageImpl<>(taskList);

    }

    @Test
    void getTaskFromUserSuccessfully() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepo.findByUserId(1L, pageable)).thenReturn(taskPage);

        Page<Task> result = userService.getTasksByUser(1L, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Title", result.getContent().get(0).getTitle());
        assertEquals("Title 2", result.getContent().get(1).getTitle());
    }

    @Test
    void throwEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getTasksByUser(1L, pageable));
    }


    @Test
    void changeStatusSuccessfully() throws AccessDeniedException {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        userService.changeStatus(1L, user, status);

        verify(taskRepo, times(1)).save(task);
        assertEquals(Status.COMPLETED, task.getStatus());
    }

    @Test
    void throwAccessDeniedExceptionWhenUserIsNotOwner(){
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        assertThrows(AccessDeniedException.class, () -> userService.changeStatus(1L, otherUser, status));
        verify(taskRepo, never()).save(any());
    }

    @Test
    void throwEntityNotFoundExceptionWhenTaskNotFound() throws EntityNotFoundException {
        when(taskRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.changeStatus(1L, user, status));
        verify(taskRepo, never()).save(any());
    }


    @Test
    void addCommentSuccessfully() throws AccessDeniedException {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        userService.addComment("Comment", user, 1L);

        verify(taskRepo, times(1)).save(task);
        assertEquals("Comment", task.getComment());
    }

    @Test
    void throwsAccessDeniedExceptionWhenUserIsNotOwner(){
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        assertThrows(AccessDeniedException.class,
                () -> userService.addComment("Comment", otherUser, 1L));
        verify(taskRepo, never()).save(any());
    }

    @Test
    void throwsEntityNotFoundExceptionWhenTaskNotFound() throws EntityNotFoundException {
        when(taskRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.addComment("Comment", user, 1L));
        verify(taskRepo, never()).save(any());
    }
}
