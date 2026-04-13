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
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
@Validated
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
    @Operation(summary = "Проверяет, приобрёл ли пользователь указанный курс")
    public boolean hasPurchased(
            @NotNull(message = "ID пользователя не может быть пустым")
            @RequestParam Long userId,
            @NotNull(message = "ID курса не может быть пустым")
            @RequestParam Long courseId
    ) {
        service.checkUserAndCourseExists(userId, courseId);
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
    @Operation(summary = " Возвращает историю покупок пользователя с пагинацией")
    public PageDto<PurchaseDto> getHistory(
            @NotNull(message = "ID пользователя не может быть пустым")
            @RequestParam Long userId,
            @PageableDefault(sort = "created", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return PageDto.of(service.getUserHistory(userId, pageable));
    }


    /**
     * Возвращает самый продаваемый курс за всё время.
     *
     * @return объект с информацией о курсе ({@link TopCourseProjection})
     */
    @GetMapping("/top-course")
    @Operation(summary = "Возвращает самый продаваемый курс за всё время")
    public ResponseEntity<TopCourseProjection> getTopSellingCourse() {
        return service.findTopSellingCourse()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    /**
     * Позволяет пользователю купить курс.
     * Если курс уже куплен — возвращается ошибка 409.
     * Если пользователь подписан на скидки — цена снижается на 15%.
     */
    @PostMapping
    @Operation(summary = "Покупка курса пользователем")
    public ResponseEntity<PurchaseDto> purchaseCourse(
            @NotNull(message = "ID пользователя не может быть пустым")
            @RequestParam Long userId,
            @NotNull(message = "ID курса не может быть пустым")
            @RequestParam Long courseId
    ) {
        PurchaseDto purchaseDto = service.purchaseCourse(userId, courseId);
        return ResponseEntity.ok(purchaseDto);
    }
}