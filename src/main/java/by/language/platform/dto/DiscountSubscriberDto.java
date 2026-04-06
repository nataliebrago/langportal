package by.language.platform.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "E-mail, подписанный на рассылку скидок")
public record DiscountSubscriberDto(
        @Schema(description = "Идентификатор", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "E-mail для рассылки", example = "subscriber@mail.ru")
        @NotBlank
        @Email
        @Size(max = 120)
        String email,

        @Schema(description = "Дата/время создания записи", type = "string", format = "date-time",
                accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime created
) {
}