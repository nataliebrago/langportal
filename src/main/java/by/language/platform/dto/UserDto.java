package by.language.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Информация о пользователе")
public record UserDto(
        @Schema(description = "Идентификатор", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Электронная почта", example = "ivan@mail.ru")
        @NotBlank
        @Email
        @Size(max = 120)
        String email,

        @Schema(description = "Фамилия", example = "Иванов")
        @Size(max = 120)
        String surname,


        @Schema(description = "Имя", example = "Иван")
        @Size(max = 120)
        String name,

        @Schema(description = "E-mail подтверждён", example = "true")
        boolean registered
) {}