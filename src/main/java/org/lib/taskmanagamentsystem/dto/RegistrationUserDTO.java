package org.lib.taskmanagamentsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Сущность пользователя для регистрации")
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationUserDTO {

    @Schema(description = "Юзернейм", example = "user")
    private String username;

    @Schema(description = "Почта пользователя", example = "user@gmail.com")
    private String email;

    @Schema(description = "пароль", example = "password")
    private String password;
}
