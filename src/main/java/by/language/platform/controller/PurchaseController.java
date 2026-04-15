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
import by.language.platform.model.Role;
import by.language.platform.model.User;
import by.language.platform.service.PurchaseService;
import by.language.platform.dto.PurchaseDto;
import by.language.platform.repository.PurchaseRepository.TopCourseProjection;
import by.language.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
@Validated
public class PurchaseController {

    private final PurchaseService service;
    private final UserService userService;

    /**
     * Проверяет, приобрёл ли пользователь указанный курс.
     *
     * @param userId   ID пользователя
     * @param courseId ID курса
     * @return {@code true}, если курс куплен; иначе — {@code false}
     */
    @GetMapping("/check")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Проверяет, приобрёл ли пользователь указанный курс")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результат проверки"),
            @ApiResponse(responseCode = "403", description = "Попытка проверить чужую покупку"),
            @ApiResponse(responseCode = "400", description = "ID пользователя или курса не указаны"),
            @ApiResponse(responseCode = "404", description = "Пользователь или курс не найден")
    })
    public ResponseEntity<Boolean> hasPurchased(
            @NotNull(message = "ID пользователя не может быть пустым")
            @RequestParam Long userId,
            @NotNull(message = "ID курса не может быть пустым")
            @RequestParam Long courseId,
            Authentication auth
    ) {
        String username = auth.getName();
        User currentUser = userService.findByEmail(username);

        if (!userService.existsById(userId)) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        if (currentUser.getRole().equals(Role.USER) && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        service.checkUserAndCourseExists(userId, courseId);
        boolean isPurchase = service.hasPurchased(userId, courseId);
        return ResponseEntity.ok(isPurchase);
    }


    /**
     * Возвращает историю покупок пользователя с пагинацией.
     * <p>
     * Результат автоматически сортируется по дате создания в порядке убывания.
     *
     * @param userId   ID пользователя
     * @param pageable параметры пагинации (page, size, sort)
     * @return страница объектов {@link PurchaseDto}
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результат проверки"),
            @ApiResponse(responseCode = "403", description = "Попытка проверить чужую историю"),
            @ApiResponse(responseCode = "400", description = "ID пользователя или курса не указаны"),
            @ApiResponse(responseCode = "404", description = "Пользователь или курс не найден")
    })
    @Operation(summary = " Возвращает историю покупок пользователя с пагинацией")
    public ResponseEntity<PageDto<PurchaseDto>> getHistory(
            @NotNull(message = "ID пользователя не может быть пустым")
            @RequestParam Long userId,
            @PageableDefault(sort = "created", direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication auth
    ) {
        String username = auth.getName();
        User currentUser = userService.findByEmail(username);

        if (!userService.existsById(userId)) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        if (currentUser.getRole().equals(Role.USER) && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(PageDto.of(service.getUserHistory(userId, pageable)));
    }


    /**
     * Возвращает самый продаваемый курс за всё время.
     *
     * @return объект с информацией о курсе ({@link TopCourseProjection})
     */
    @GetMapping("/top-course")
    @PreAuthorize("permitAll()")
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
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Покупка курса пользователем")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результат покупки"),
            @ApiResponse(responseCode = "403", description = "Попытка купить курс не для себя"),
            @ApiResponse(responseCode = "400", description = "ID пользователя или курса не указаны"),
            @ApiResponse(responseCode = "404", description = "Пользователь или курс не найден")
    })
    public ResponseEntity<PurchaseDto> purchaseCourse(
            @NotNull(message = "ID пользователя не может быть пустым")
            @RequestParam Long userId,
            @NotNull(message = "ID курса не может быть пустым")
            @RequestParam Long courseId,
            Authentication auth
    ) {

        String username = auth.getName();
        User currentUser = userService.findByEmail(username);

        if (!userService.existsById(userId)) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        if (currentUser.getRole().equals(Role.USER) && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        PurchaseDto purchaseDto = service.purchaseCourse(userId, courseId);
        return ResponseEntity.ok(purchaseDto);
    }
}