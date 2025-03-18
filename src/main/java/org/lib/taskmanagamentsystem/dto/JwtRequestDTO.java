package org.lib.taskmanagamentsystem.dto;

import lombok.Data;

@Data
public class JwtRequestDTO {

    private String email;

    private String password;
}
