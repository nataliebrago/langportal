package by.language.platform.controller;

import by.language.platform.dto.DiscountSubscriberDto;
import by.language.platform.service.DiscountSubscriberService;
import by.language.platform.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/discount-subscribers")
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
    public ResponseEntity<?> subscribe(@RequestBody @Valid EmailRequest request) {
        // Проверяем, существует ли пользователь с таким email
        if (!userService.existsByEmail(request.email())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Пользователь с email '%s' не зарегистрирован".formatted(request.email())));
        }

        // Проверка: уже подписан?
        Optional<DiscountSubscriberDto> existing = service.findByEmail(request.email());
        if (existing.isPresent()) {
            return ResponseEntity.status(409).body(new ErrorResponse("Пользователь с email '%s' уже подписан".formatted(request.email())));
        }

        // Создаём нового подписчика
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
    public boolean existsByEmail(@RequestParam @Email String email) {
        return service.existsByEmail(email);
    }

    /**
     * Возвращает данные подписчика по email.
     *
     * @param email email пользователя
     * @return DTO подписчика или статус 404, если не найден
     */
    @GetMapping("/by-email")
    public ResponseEntity<DiscountSubscriberDto> findByEmail(@RequestParam @Email String email) {
        return service.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Возвращает список новых подписчиков за последние 7 дней.
     *
     * @param weekAgo начало периода (по умолчанию 7 дней назад)
     * @return список DTO подписчиков
     */
    @GetMapping("/new")
    public List<DiscountSubscriberDto> getNewSubscribers(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime weekAgo
    ) {
        if (weekAgo == null) {
            weekAgo = LocalDateTime.now().minusDays(7);
        }
        return service.findNewSubscribers(weekAgo);
    }

    /**
     * Возвращает общее количество подписчиков на скидки.
     *
     * @return общее число подписчиков
     */
    @GetMapping("/count")
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