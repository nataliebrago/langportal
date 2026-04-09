package by.language.platform.service;
/**
 * Сервис для управления покупками пользователей.
 *
 * Обеспечивает бизнес-логику, связанную с операциями покупки курсов:
 * - Проверка, приобретён ли курс пользователем
 * - Получение истории покупок пользователя с пагинацией
 * - Подсчёт общей выручки за указанный период
 * - Количество покупок за день
 * - Определение самого популярного курса (по числу продаж)
 */

import by.language.platform.dto.PurchaseDto;
import by.language.platform.mapper.PurchaseMapper;
import by.language.platform.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import by.language.platform.repository.PurchaseRepository.TopCourseProjection;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {

    private final PurchaseRepository repo;
    private final PurchaseMapper mapper;

    /**
     * Проверяет, приобрёл ли пользователь курс с заданным ID.
     *
     * @param userId   идентификатор пользователя
     * @param courseId идентификатор курса
     * @return {@code true}, если пользователь уже купил курс; иначе — {@code false}
     */
    public boolean hasPurchased(Long userId, Long courseId) {
        return repo.existsByUserIdAndCourseId(userId, courseId);
    }

    /**
     * Возвращает страницу истории покупок указанного пользователя.
     *
     * Данные включают информацию о курсе (название, преподаватель и т.п.)
     *
     * @param userId   идентификатор пользователя
     * @param pageable параметры пагинации и сортировки
     * @return страница объектов {@link PurchaseDto}
     */
    public Page<PurchaseDto> getUserHistory(Long userId, Pageable pageable) {
        return repo.findByUserIdOrderByCreatedDesc(userId, pageable)
                .map(mapper::toDto);
    }

    /**
     * Суммирует общую выручку по всем покупкам в указанном временном интервале.
     *
     * @param from начало периода (включительно)
     * @param to   конец периода (не включительно)
     * @return сумма всех оплаченных сумм или {@code null}, если покупок не было
     */
    public BigDecimal sumPaidBetween(LocalDateTime from, LocalDateTime to) {
        return repo.sumPaidBetween(from, to).orElse(null);
    }

    /**
     * Подсчитывает количество покупок, совершённых в течение одного дня.
     *
     * @param day      начало дня (например, 2025-04-05T00:00:00)
     * @param dayPlus1 начало следующего дня (например, 2025-04-06T00:00:00)
     * @return количество покупок
     */
    public long countToday(LocalDateTime day, LocalDateTime dayPlus1) {
        return repo.countToday(day, dayPlus1);
    }

    /**
     * Находит самый продаваемый курс за всё время на основе количества покупок.
     *
     * Использует нативный SQL-запрос и возвращает проекцию с минимальными данными.
     *
     */
    public Optional<TopCourseProjection> findTopSellingCourse() {
        return repo.findTopSellingCourse();
    }

}