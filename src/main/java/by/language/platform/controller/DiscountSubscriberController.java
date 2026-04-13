package by.language.platform.controller;

import by.language.platform.dto.DiscountSubscriberDto;
import by.language.platform.service.DiscountSubscriberService;
import by.language.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/discount-subscribers")
@RequiredArgsConstructor
@Validated
public class DiscountSubscriberController {

    private final DiscountSubscriberService service;
    private final UserService userService;


    /**
     * Подписывает email на получение информации о скидках.
     * <p>
     * Проверяет:
     * - Корректность email формата
     * - Существует ли пользователь с таким email
     *
     * @param request содержит email для подписки
     * @return DTO созданного подписчика
     */

    @PostMapping
    @Operation(summary = "Подписывает email на получение информации о скидках.")
    public ResponseEntity<?> subscribe(@RequestBody @Valid EmailRequest request) {
        if (!userService.existsByEmail(request.email())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Пользователь с email '%s' не зарегистрирован".formatted(request.email())));
        }

        Optional<DiscountSubscriberDto> existing = service.findByEmail(request.email());
        if (existing.isPresent()) {
            return ResponseEntity.status(409).body(new ErrorResponse("Пользователь с email '%s' уже подписан".formatted(request.email())));
        }

        DiscountSubscriberDto dto = service.subscribe(request.email());
        return ResponseEntity
                .created(URI.create("/api/discount-subscribers/" + dto.id()))
                .body(dto);
    }

    /**
     * Проверяет, подписан ли пользователь с указанным email.
     *
     * @param email email для проверки
     * @return true, если подписан; иначе false
     */
    @GetMapping("/exists")
    @Operation(summary = "Проверяет, подписан ли пользователь с указанным email")
    public boolean existsByEmail(@RequestParam
                                 @Email(message = "Некорректный формат email")
                                 @NotBlank(message = "Email не может быть пустым")
                                 String email) {
        return service.existsByEmail(email);
    }

    /**
     * Возвращает данные подписчика по email.
     *
     * @param email email пользователя
     * @return DTO подписчика или статус 404, если не найден
     */
    @GetMapping("/by-email")
    @Operation(summary = "Возвращает данные подписчика по email")
    public ResponseEntity<?> findByEmail(
            @RequestParam
            @Email(message = "Некорректный формат email")
            @NotBlank(message = "Email не может быть пустым")
            String email) {

        return service.findByEmail(email)
                .map(dto -> ResponseEntity.ok((Object) dto))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Подписчик с email '%s' не найден".formatted(email))));
    }

    /**
     * Возвращает общее количество подписчиков на скидки.
     *
     * @return общее число подписчиков
     */
    @GetMapping("/count")
    @Operation(summary = "Возвращает общее количество подписчиков на скидки")
    public long getTotalCount() {
        return service.countAllSubscribers();
    }

    /**
     * DTO для получения email из тела запроса.
     */
    public record EmailRequest(@NotBlank(message = "Email не может быть пустым")
                               @Email(message = "Некорректный формат email")
                               String email) {
    }

    public record ErrorResponse(String message) {
    }

}