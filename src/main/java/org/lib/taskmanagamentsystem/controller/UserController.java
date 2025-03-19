package org.lib.taskmanagamentsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.lib.taskmanagamentsystem.entity.Status;
import org.lib.taskmanagamentsystem.entity.User;
import org.lib.taskmanagamentsystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "User_controller")
@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @Operation(
            summary = "Изменение статуса задачи",
            description = "Принимает taskId из адреса и новый status, после обновляет поле задачи на новое значение"
    )
    @PutMapping("/change_status/{taskId}")
    public ResponseEntity<String> changeStatus(@RequestBody Status status,
                                               @AuthenticationPrincipal User user,
                                               @PathVariable Long taskId) {
        return userService.changeStatus(status, user, taskId);
    }


    @Operation(
            summary = "Добавление комментария к задаче",
            description = "Принимает taskId из адреса и comment, после обновляет поле задачи на новое значение"
    )
    @PutMapping("/add_comment/{taskId}")
    public ResponseEntity<String> addComment(@RequestBody String comment,
                                             @AuthenticationPrincipal User user,
                                             @PathVariable Long taskId) {
        return userService.addComment(comment, user, taskId);
    }
}
