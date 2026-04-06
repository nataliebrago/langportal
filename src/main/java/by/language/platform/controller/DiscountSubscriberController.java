package by.language.platform.controller;

import by.language.platform.dto.DiscountSubscriberDto;
import by.language.platform.service.DiscountSubscriberService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/discount-subscribers")
@RequiredArgsConstructor
public class DiscountSubscriberController {

    private final DiscountSubscriberService service;

    /**
     * Подписывает пользователя на скидки по email.
     *
     * @param request объект с email
     * @return DTO созданного подписчика
     */
    @PostMapping
    public ResponseEntity<DiscountSubscriberDto> subscribe(@RequestBody EmailRequest request) {
        DiscountSubscriberDto dto = service.subscribe(request.email());
        return ResponseEntity.ok(dto);
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
        DiscountSubscriberDto dto = service.findByEmail(email);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
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
    public record EmailRequest(@Email(message = "Некорректный формат email") String email) {}
}