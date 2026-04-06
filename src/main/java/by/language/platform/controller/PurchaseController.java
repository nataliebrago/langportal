package by.language.platform.controller;

/**
 * REST-контроллер для работы с покупками курсов.
 * <p>
 * Предоставляет API-эндпоинты для:
 * - Проверки факта покупки курса пользователем
 * - Получения истории покупок пользователя
 * - Сбора статистики по выручке и активности
 * - Определения самого популярного курса
 */

import by.language.platform.dto.PageDto;
import by.language.platform.service.PurchaseService;
import by.language.platform.dto.PurchaseDto;
import by.language.platform.repository.PurchaseRepository.TopCourseProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService service;

    /**
     * Проверяет, приобрёл ли пользователь указанный курс.
     *
     * @param userId   ID пользователя
     * @param courseId ID курса
     * @return {@code true}, если курс куплен; иначе — {@code false}
     */
    @GetMapping("/check")
    public boolean hasPurchased(
            @RequestParam Long userId,
            @RequestParam Long courseId
    ) {
        return service.hasPurchased(userId, courseId);
    }

    /**
     * Возвращает историю покупок пользователя с пагинацией.
     *
     * Результат автоматически сортируется по дате создания в порядке убывания.
     *
     * @param userId   ID пользователя
     * @param pageable параметры пагинации (page, size, sort)
     * @return страница объектов {@link PurchaseDto}
     */
    @GetMapping("/history")
    public PageDto<PurchaseDto> getHistory(
            @RequestParam Long userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return PageDto.of(service.getUserHistory(userId, pageable));
    }

    /**
     * Возвращает общую сумму выручки за указанный временной интервал.
     *
     * @param from начало периода (ISO формат: yyyy-MM-dd'T'HH:mm:ss)
     * @param to   конец периода
     * @return сумма всех оплат в указанном диапазоне или 0, если нет данных
     */
    @GetMapping("/revenue")
    public BigDecimal getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return service.sumPaidBetween(from, to);
    }

    /**
     * Возвращает количество покупок, совершённых в указанный день.
     *
     * @param date дата (время игнорируется, берётся весь день)
     * @return количество покупок
     */
    @GetMapping("/count-today")
    public long getCountToday(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return service.countToday(start, end);
    }

    /**
     * Возвращает самый продаваемый курс за всё время.
     *
     * @return объект с информацией о курсе ({@link TopCourseProjection})
     */
    @GetMapping("/top-course")
    public ResponseEntity<TopCourseProjection> getTopSellingCourse() {
        return service.findTopSellingCourse()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}