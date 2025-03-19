package org.lib.taskmanagamentsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "JWT ответ")
public class JwtResponseDTO {

    @Schema(description = "Токен доступа")
    private String accessToken;

    @Schema(description = "Токен обновления")
    private String refreshToken;
}
