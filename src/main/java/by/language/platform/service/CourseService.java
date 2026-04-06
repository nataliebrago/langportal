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
import by.language.platform.exception.CourseNotFoundException;
import by.language.platform.mapper.CourseMapper;
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
     * Находит курс по ID для безопасного обновления цены.
     *
     * Использует пессимистичную блокировку строки в БД, чтобы предотвратить
     * параллельные изменения цены (например, при массовой акции).
     *
     * @param id идентификатор курса
     * @return DTO найденного курса
     * @throws CourseNotFoundException если курс не найден
     */
    @Transactional
    public CourseDto findByIdForPriceUpdate(Long id) {
        return repo.findByIdForPriceUpdate(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    /**
     * Обновляет цену курса.
     *
     * Требует пессимистичной блокировки — вызовите {@link #findByIdForPriceUpdate(Long)} перед этим.
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
            course.setPrice(newPrice.doubleValue()); // Преобразуем в double
        }
        return mapper.toDto(repo.save(course));
    }
}