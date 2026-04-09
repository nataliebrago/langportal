package by.language.platform.repository;


import by.language.platform.model.User;
import by.language.platform.model.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Покупки = связка «пользователь ↔ курс ↔ цена продажи».
 */
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {


    List<Purchase> findByUser(User user);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);


    @Query("select sum(p.paidAmount) from Purchase p where p.created between :from and :to")
    Optional<BigDecimal> sumPaidBetween(@Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to);

    /**
     * Кол-во покупок за день.
     */
    @Query("select count(p) from Purchase p where p.created >= :day and p.created < :dayPlus1")
    long countToday(@Param("day") LocalDateTime day,
                    @Param("dayPlus1") LocalDateTime dayPlus1);


    /**
     * История покупок пользователя (сразу подтягиваем курс).
     */
    @EntityGraph(attributePaths = "course")
    Page<Purchase> findByUserIdOrderByCreatedDesc(Long userId, Pageable pageable);


    /**
     * Самый продаваемый курс за всё время.
     * Возвращает короткую проекцию.
     */
    @Query(value = """
            SELECT c.id, c.title, COUNT(p.id) AS cnt
            FROM purchases p
            JOIN courses c ON p.course_id = c.id
            GROUP BY c.id, c.title
            ORDER BY cnt DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<TopCourseProjection> findTopSellingCourse();

    interface TopCourseProjection {
        Long getId();

        String getTitle();

        Long getCnt();
    }
}