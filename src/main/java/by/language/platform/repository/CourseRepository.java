package by.language.platform.repository;

import by.language.platform.model.Course;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий сущности {@link Course}.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTitleContainingIgnoreCase(String title);


    @Query("select c from Course c where c.price < :maxPrice")
    List<Course> findCheaperThan(@Param("maxPrice") BigDecimal maxPrice);

    @Query("select c from Course c where c.price between :min and :max")
    Page<Course> findByPriceRange(@Param("min") BigDecimal min,
                                  @Param("max") BigDecimal max,
                                  Pageable pageable);

    /**
     * ТОП-3 самых дорогих курсов.
     * native – чтобы использовать LIMIT без Pageable.
     */
    @Query(value = "SELECT * FROM courses ORDER BY price DESC LIMIT 3", nativeQuery = true)
    List<Course> findTop3Expensive();

    /**
     * Средняя цена курса.
     */
    @Query("select avg(c.price) from Course c")
    Optional<BigDecimal> averagePrice();

    /**
     * Бронируем строку при обновлении цены (акция 1 день).
     * Используется в админке.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Course c where c.id = :id")
    Optional<Course> findByIdForPriceUpdate(@Param("id") Long id);
}