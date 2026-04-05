package by.language.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Запрос на создание пользователя")
public record UserCreateDto(
        @Schema(description = "Электронная почта", example = "ivan@mail.ru")
        @NotBlank
        @Email
        @Size(max = 120)
        String email,

        @Schema(description = "Пароль (мин. 6 символов)", example = "qwerty123")
        @NotBlank
        @Size(min = 6, max = 120)
        String password,

        @Schema(description = "Фамилия", example = "Иванов")
        @Size(max = 120)
        String surname,

        @Schema(description = "Имя", example = "Иван")
        @Size(max = 120)
        String name
) {
}