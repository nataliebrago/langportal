package by.language.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateCourseRequest(
        @NotBlank(message = "Название обязательно")
        String title,

        @Positive(message = "Цена должна быть положительной")
        BigDecimal price
) {}
