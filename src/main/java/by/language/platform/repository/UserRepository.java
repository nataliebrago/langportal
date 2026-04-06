package by.language.platform.repository;



import by.language.platform.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;


/**
 * Репозиторий сущности {@link User}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Email уникален в БД → Optional удобно сразу кидать Exception. */
    Optional<User> findByEmail(String email);

    /** Проверка уникальности перед созданием. */
    boolean existsByEmail(String email);

    /** Все подтвердившие e-mail (registered = true). */
    @Query("select u from User u where u.registered = true")
    Page<User> findAllConfirmed(Pageable pageable);

    /** Поиск по подстроке в имени или фамилии. */
    @Query("select u from User u where lower(u.name) like lower(concat('%',:text,'%')) " +
            "or lower(u.surname) like lower(concat('%',:text,'%'))")
    Page<User> search(@Param("text") String text, Pageable pageable);


    /**
     * Обновить пароль в одном запросе.
     * Возвращает кол-во изменённых строк (0 – пользователь не найден).
     */
    @Modifying(clearAutomatically = true)  // clear чтобы 1-й уровень кэша не хранил старый пароль
    @Query("update User u set u.password = :pwd, u.created = :now where u.id = :id")
    int updatePassword(@Param("id") Long id,
                       @Param("pwd") String encodedPwd,
                       @Param("now") LocalDateTime now);


    /** Все покупки пользователя. */
    @EntityGraph(attributePaths = "purchases")
    Optional<User> findWithPurchasesById(Long id);

}