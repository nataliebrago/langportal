package by.language.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Курс языковой школы")
public record CourseDto(
        @Schema(description = "Идентификатор", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Название курса", example = "English A1")
        @NotBlank
        String title,

        @Schema(description = "Цена", example = "199.99")
        @Positive
        BigDecimal price
) {}