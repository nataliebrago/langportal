package by.language.platform.controller;

import by.language.platform.dto.CourseDto;
import by.language.platform.dto.CreateCourseRequest;
import by.language.platform.dto.PageDto;
import by.language.platform.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;

    /**
     * Создаёт новый курс.
     *
     * @param request данные для создания курса
     * @return DTO созданного курса
     */
    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@RequestBody CreateCourseRequest request) {
        CourseDto courseDto = service.createCourse(request);
        return ResponseEntity
                .created(URI.create("/api/courses/" + courseDto.id()))
                .body(courseDto);
    }


    /**
     * Ищет курсы по части названия (без учёта регистра).
     *
     * @param title подстрока для поиска
     * @return список курсов, содержащих подстроку в названии
     */
    @GetMapping("/search")
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
    public List<CourseDto> findCheaperThan(@RequestParam BigDecimal maxPrice) {
        return service.findCheaperThan(maxPrice);
    }

    /**
     * Возвращает страницу курсов в заданном диапазоне цен.
     *
     * @param min      минимальная цена
     * @param max      максимальная цена
     * @param pageable параметры пагинации (page, size, sort)
     * @return страница курсов
     */
    @GetMapping("/by-price")
    public PageDto<CourseDto> findByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return PageDto.of(service.findByPriceRange(min, max, pageable));
    }

    /**
     * Возвращает 3 самых дорогих курса платформы.
     *
     * @return список из трёх курсов
     */
    @GetMapping("/top-expensive")
    public List<CourseDto> findTop3Expensive() {
        return service.findTop3Expensive();
    }

    /**
     * Возвращает среднюю цену всех курсов.
     *
     * @return средняя цена или 0, если курсов нет
     */
    @GetMapping("/average-price")
    public BigDecimal getAveragePrice() {
        return service.getAveragePrice();
    }

    /**
     * Обновляет цену курса.
     *
     * Рекомендуется вызывать после {@code /for-price-update}, чтобы избежать гонки условий.
     *
     * @param id       идентификатор курса
     * @param request  объект с новой ценой (в поле "price")
     * @return обновлённый курс
     */
    @PatchMapping("/{id}/price")
    public ResponseEntity<CourseDto> updatePrice(
            @PathVariable Long id,
            @RequestBody PriceUpdateRequest request
    ) {
        CourseDto updated = service.updatePrice(id, request.price());
        return ResponseEntity.ok(updated);
    }

    /**
     * DTO для обновления цены курса.
     */
    public record PriceUpdateRequest(BigDecimal price) {}
}