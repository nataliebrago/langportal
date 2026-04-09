/**
 * Сервис для управления курсами в образовательной платформе.
 *
 * Обеспечивает бизнес-логику:
 * - Поиск курсов по названию, цене и диапазону
 * - Получение топовых и статистических данных
 * - Пессимистичная блокировка при изменении цены (для админки)
 */
package by.language.platform.service;

import by.language.platform.dto.CourseDto;
import by.language.platform.dto.CreateCourseRequest;
import by.language.platform.exception.CourseNotFoundException;
import by.language.platform.mapper.CourseMapper;
import by.language.platform.model.Course;
import by.language.platform.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository repo;
    private final CourseMapper mapper;

    /**
     * Создаёт новый курс.
     *
     * @param request DTO с данными курса
     * @return DTO созданного курса
     */
    @Transactional
    public CourseDto createCourse(CreateCourseRequest request) {
        Course course = new Course(request.title(), request.price());
        Course saved = repo.save(course);
        return mapper.toDto(saved);
    }

    /**
     * Находит курсы, чьё название содержит указанную подстроку (без учёта регистра).
     *
     * @param title часть названия курса
     * @return список курсов, подходящих под критерий
     */
    public List<CourseDto> searchByTitle(String title) {
        return repo.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Возвращает все курсы дешевле указанной цены.
     *
     * @param maxPrice максимальная цена (исключительно)
     * @return список курсов с ценой < maxPrice
     */
    public List<CourseDto> findCheaperThan(BigDecimal maxPrice) {
        return repo.findCheaperThan(maxPrice)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Возвращает страницу курсов в заданном диапазоне цен.
     *
     * Поддерживает пагинацию и сортировку.
     *
     * @param min      минимальная цена (включительно)
     * @param max      максимальная цена (включительно)
     * @param pageable параметры пагинации
     * @return страница курсов
     */
    public Page<CourseDto> findByPriceRange(BigDecimal min, BigDecimal max, Pageable pageable) {
        return repo.findByPriceRange(min, max, pageable)
                .map(mapper::toDto);
    }

    /**
     * Возвращает 3 самых дорогих курса платформы.
     *
     * @return список из трёх курсов, отсортированных по убыванию цены
     */
    public List<CourseDto> findTop3Expensive() {
        return repo.findTop3Expensive()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Вычисляет среднюю цену всех курсов.
     * @return средняя цена или null, если курсов нет
     */
    public BigDecimal getAveragePrice() {
        return repo.averagePrice().orElse(null);
    }

    /**
     * Обновляет цену курса.
     *
     *
     * @param id   идентификатор курса
     * @param newPrice новая цена
     * @return обновлённый курс
     * @throws CourseNotFoundException если курс не найден
     */
    @Transactional
    public CourseDto updatePrice(Long id, BigDecimal newPrice) {
        var course = repo.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        if (newPrice != null) {
            course.setPrice(newPrice); // Преобразуем в double
        }
        return mapper.toDto(repo.save(course));
    }
}