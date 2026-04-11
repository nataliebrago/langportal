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

    boolean existsByTitleIgnoreCase(String title);

    @Query("select c from Course c where c.price < :maxPrice")
    List<Course> findCheaperThan(@Param("maxPrice") BigDecimal maxPrice);

}