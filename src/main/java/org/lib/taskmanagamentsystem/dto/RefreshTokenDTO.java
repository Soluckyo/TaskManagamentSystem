package org.lib.taskmanagamentsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Токен обновления")
public class RefreshTokenDTO {

    @Schema(description = "Токен обновления")
    private String refreshToken;
}
