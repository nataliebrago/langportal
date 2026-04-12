package by.language.platform.repository;

import by.language.platform.model.DiscountSubscriber;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Таблица «хочу скидку 10 %» – просто e-mail.
 */
@Repository
public interface DiscountSubscriberRepository extends JpaRepository<DiscountSubscriber, Long> {

    Optional<DiscountSubscriber> findByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    /**
     * Кол-во подписчиков на текущий момент времени.
     */
    @Query("select count(d) from DiscountSubscriber d")
    long countSubscribers();
}