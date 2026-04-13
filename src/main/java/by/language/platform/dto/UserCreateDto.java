package by.language.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание пользователя")
public record UserCreateDto(
        @Schema(description = "Email", example = "user@test.com", required = true)
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат email")
        @Size(max = 120)
        String email,

        @Schema(description = "Пароль", example = "qwerty", required = true)
        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
        @Size(max = 120, message = "Пароль должен быть не более 120 символов")
        String password,

        @Schema(description = "Фамилия", example = "Иванов", required = true)
        @NotBlank(message = "Фамилия не может быть пустой")
        @Size(max = 120,message = "Фамилия должна быть не более 120 символов")
        String surname,

        @Schema(description = "Имя пользователя", example = "Иван", required = true)
        @NotBlank(message = "Имя не может быть пустым")
        @Size(max = 120, message = "Имя пользователя должно  быть не более 120 символов")
        String name
) {
}