package org.lib.taskmanagamentsystem.controller;

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

@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/change_status/{taskId}")
    public ResponseEntity<String> changeStatus(@RequestBody Status status,
                                               @AuthenticationPrincipal User user,
                                               @PathVariable Long taskId) {
        return userService.changeStatus(status, user, taskId);
    }

    @PutMapping("/add_comment/{taskId}")
    public ResponseEntity<String> addComment(@RequestBody String comment,
                                             @AuthenticationPrincipal User user,
                                             @PathVariable Long taskId) {
        return userService.addComment(comment, user, taskId);
    }
}
