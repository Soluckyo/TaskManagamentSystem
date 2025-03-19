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
import org.lib.taskmanagamentsystem.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
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

    @InjectMocks
    private UserService userService;

    private Task task;
    private User user;
    private User otherUser;
    private Status status;

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

        status = Status.COMPLETED;

    }

    @Test
    void shouldChangeStatusSuccessfully() throws AccessDeniedException {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        userService.changeStatus(1L, user, status);

        verify(taskRepo, times(1)).save(task);
        assertEquals(Status.COMPLETED, task.getStatus());
    }

    @Test
    void shouldThrowAccessDeniedExceptionWhenUserIsNotOwner(){
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        assertThrows(AccessDeniedException.class, () -> userService.changeStatus(1L, otherUser, status));
        verify(taskRepo, never()).save(any());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenTaskNotFound() throws EntityNotFoundException {
        when(taskRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.changeStatus(1L, user, status));
        verify(taskRepo, never()).save(any());
    }


    @Test
    void shouldAddCommentSuccessfully() throws AccessDeniedException {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        userService.addComment("Comment", user, 1L);

        verify(taskRepo, times(1)).save(task);
        assertEquals("Comment", task.getComment());
    }

    @Test
    void shouldThrowsAccessDeniedExceptionWhenUserIsNotOwner(){
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        assertThrows(AccessDeniedException.class,
                () -> userService.addComment("Comment", otherUser, 1L));
        verify(taskRepo, never()).save(any());
    }

    @Test
    void shouldThrowsEntityNotFoundExceptionWhenTaskNotFound() throws EntityNotFoundException {
        when(taskRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.addComment("Comment", user, 1L));
        verify(taskRepo, never()).save(any());
    }
}
