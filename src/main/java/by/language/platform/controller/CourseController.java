package by.language.platform.controller;

import by.language.platform.dto.CourseDto;
import by.language.platform.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Validated
public class CourseController {

    private final CourseService service;

    /**
     * Эндпоинт для создания нового курса.
     * <p>
     * Принимает данные в формате JSON, валидирует их и передаёт на обработку в {@link CourseService}.
     * При успешном создании возвращает объект {@link CourseDto} и статус {@code 201 Created}.
     *
     * @param request объект с данными для создания курса (название, цена)
     * @return созданный курс в формате {@link CourseDto}
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание нового курса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Курс успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные (например, пустое название или цена <= 0)"),
            @ApiResponse(responseCode = "409", description = "Курс с таким названием уже существует")
    })
    public ResponseEntity<CourseDto> createCourse(@RequestBody @Valid CourseDto request) {
        CourseDto courseDto = service.createCourse(request);
        return ResponseEntity.created(URI.create("/api/courses/" + courseDto.id())).body(courseDto);
    }


    /**
     * Ищет курсы по части названия (без учёта регистра).
     *
     * @param title подстрока для поиска
     * @return список курсов, содержащих подстроку в названии
     */
    @GetMapping("/search")
    @Operation(summary = "Ищет курсы по части названия (без учёта регистра)")
    public List<CourseDto> searchByTitle(@RequestParam String title) {
        return service.searchByTitle(title);
    }

    /**
     * Возвращает все курсы дешевле указанной цены.
     *
     * @param maxPrice максимальная цена
     * @return список курсов
     */
    @GetMapping("/cheaper-than")
    @Operation(summary = "Возвращает все курсы дешевле указанной цены.")
    public List<CourseDto> findCheaperThan(@RequestParam BigDecimal maxPrice) {
        return service.findCheaperThan(maxPrice);
    }

    /**
     * Обновляет цену курса.
     *
     * @param id      идентификатор курса
     * @param request объект с новой ценой (в поле "price")
     * @return обновлённый курс
     */
    @PatchMapping("/{id}/price")
    @Operation(summary = "Обновление цены курса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Цена успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные (цена пустая или <= 0)"),
            @ApiResponse(responseCode = "404", description = "Курс не найден")
    })
    public ResponseEntity<CourseDto> updatePrice(
            @PathVariable Long id,
            @RequestBody @Valid PriceUpdateRequest request
    ) {
        CourseDto updated = service.updatePrice(id, request.price());
        return ResponseEntity.ok(updated);
    }

    /**
     * DTO для обновления цены курса.
     */
    public record PriceUpdateRequest(
            @NotNull(message = "Цена не может быть null")
            @Positive(message = "Цена должна быть больше 0")
            BigDecimal price
    ) {}
}