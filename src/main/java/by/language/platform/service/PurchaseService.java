package by.language.platform.service;
/**
 * Сервис для управления покупками пользователей.
 * <p>
 * Обеспечивает бизнес-логику, связанную с операциями покупки курсов:
 * - Проверка, приобретён ли курс пользователем
 * - Получение истории покупок пользователя с пагинацией
 * - Подсчёт общей выручки за указанный период
 * - Количество покупок за день
 * - Определение самого популярного курса (по числу продаж)
 */

import by.language.platform.dto.PurchaseDto;
import by.language.platform.exception.CourseAlreadyPurchasedException;
import by.language.platform.exception.CourseNotFoundException;
import by.language.platform.exception.UserNotFoundException;
import by.language.platform.mapper.PurchaseMapper;
import by.language.platform.model.Course;
import by.language.platform.model.Purchase;
import by.language.platform.model.User;
import by.language.platform.repository.CourseRepository;
import by.language.platform.repository.DiscountSubscriberRepository;
import by.language.platform.repository.PurchaseRepository;
import by.language.platform.repository.UserRepository;
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

    private final DiscountSubscriberRepository discountSubscriberRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper mapper;

    /**
     * Проверяет, приобрёл ли пользователь курс с заданным ID.
     *
     * @param userId   идентификатор пользователя
     * @param courseId идентификатор курса
     * @return {@code true}, если пользователь уже купил курс; иначе — {@code false}
     */
    public boolean hasPurchased(Long userId, Long courseId) {
        return purchaseRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    /**
     * Возвращает страницу истории покупок указанного пользователя.
     * <p>
     * Данные включают информацию о курсе (название, преподаватель и т.п.)
     *
     * @param userId   идентификатор пользователя
     * @param pageable параметры пагинации и сортировки
     * @return страница объектов {@link PurchaseDto}
     */
    public Page<PurchaseDto> getUserHistory(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return purchaseRepository.findByUserIdOrderByCreatedDesc(userId, pageable)
                .map(mapper::toDto);
    }


    /**
     * Находит самый продаваемый курс за всё время на основе количества покупок.
     * <p>
     * Использует нативный SQL-запрос и возвращает проекцию с минимальными данными.
     */
    public Optional<TopCourseProjection> findTopSellingCourse() {
        return purchaseRepository.findTopSellingCourse();
    }


    /**
     * Выполняет покупку курса пользователем с возможным применением скидки.
     * <p>
     * Операция выполняется в рамках одной транзакции:
     * - Проверяется существование пользователя и курса
     * - Рассчитывается финальная цена (с учётом подписки на скидки)
     * - Создаётся запись о покупке и сохраняется в БД
     * <p>
     * Если на любом этапе возникает ошибка (например, пользователь не найден),
     * транзакция откатывается, и данные не сохраняются.
     *
     * @param userId   ID пользователя, который совершает покупку
     * @param courseId ID курса, который покупается
     * @return DTO созданной покупки с применённой ценой
     * @throws UserNotFoundException   если пользователь с указанным ID не найден
     * @throws CourseNotFoundException если курс с указанным ID не найден
     */
    @Transactional
    public PurchaseDto purchaseCourse(Long userId, Long courseId) {
        // Проверяем, существует ли пользователь
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Проверяем, существует ли курс
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        // Проверяем, не куплен ли уже курс
        if (purchaseRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new CourseAlreadyPurchasedException(userId, courseId);
        }

        // Базовая цена курса
        BigDecimal basePrice = course.getPrice();
        BigDecimal finalPrice = basePrice;

        // Применяем скидку 15%, если пользователь подписан
        if (discountSubscriberRepository.existsByEmail(user.getEmail())) {
            finalPrice = basePrice.multiply(BigDecimal.valueOf(0.85)); // -15%
        }

        Purchase purchase = new Purchase(user, course, finalPrice, LocalDateTime.now());
        Purchase saved = purchaseRepository.save(purchase);
        return mapper.toDto(saved);
    }

    /**
     * Проверяет, что пользователь и курс существуют в базе.
     * Выбрасывает исключение, если один из них не найден.
     */
    @Transactional(readOnly = true)
    public void checkUserAndCourseExists(Long userId, Long courseId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException(courseId);
        }
    }
}