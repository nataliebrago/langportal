package by.language.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Информация о покупке")
public record PurchaseDto(
        @Schema(description = "Идентификатор покупки", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Пользователь")
        UserDto user,

        @Schema(description = "Купленный курс")
        CourseDto course,

        @Schema(description = "Сумма, которую реально заплатили", example = "149.99")
        @Positive
        BigDecimal paidAmount
) {}