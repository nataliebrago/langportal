package by.language.platform.repository;



import by.language.platform.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Репозиторий сущности {@link User}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Проверка уникальности перед созданием. */
    boolean existsByEmail(String email);

    /** Все подтвердившие e-mail (registered = true). */
    @Query("select u from User u where u.registered = true")
    Page<User> findAllConfirmed(Pageable pageable);

}