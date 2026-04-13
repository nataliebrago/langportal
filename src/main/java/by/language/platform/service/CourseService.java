/**
 * Сервис для управления курсами в образовательной платформе.
 * <p>
 * Обеспечивает бизнес-логику:
 * - Поиск курсов по названию, цене и диапазону
 * - Получение топовых и статистических данных
 * - Пессимистичная блокировка при изменении цены (для админки)
 */
package by.language.platform.service;

import by.language.platform.dto.CourseDto;
import by.language.platform.exception.CourseAlreadyExistsException;
import by.language.platform.exception.CourseNotFoundException;
import by.language.platform.exception.CoursePriceException;
import by.language.platform.mapper.CourseMapper;
import by.language.platform.model.Course;
import by.language.platform.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
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
     * Обновляет цену курса.
     *
     * @param id       идентификатор курса
     * @param newPrice новая цена
     * @return обновлённый курс
     * @throws CourseNotFoundException если курс не найден
     */
    @Transactional
    public CourseDto updatePrice(Long id, BigDecimal newPrice) {
        var course = repo.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CoursePriceException();
        }
        if (newPrice != null) {
            course.setPrice(newPrice);
        }
        return mapper.toDto(repo.save(course));
    }

    /**
     * Создаёт новый курс.
     *
     * @param request DTO с данными курса
     * @return DTO созданного курса
     */

    @Transactional
    public CourseDto createCourse(CourseDto request) {
        if (repo.existsByTitleIgnoreCase(request.title())) {
            throw new CourseAlreadyExistsException(request.title());
        }
        Course course = mapper.toEntity(request);
        Course saved = repo.save(course);
        return mapper.toDto(saved);
    }
}