package by.language.platform.service;

import by.language.platform.dto.DiscountSubscriberDto;
import by.language.platform.mapper.DiscountSubscriberMapper;
import by.language.platform.repository.DiscountSubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscountSubscriberService {

    private final DiscountSubscriberRepository repo;
    private final DiscountSubscriberMapper mapper;

    /**
     * Проверяет, существует ли подписчик с указанным email.
     *
     * @param email email пользователя
     * @return true, если уже подписан
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return repo.existsByEmailIgnoreCase(email.trim());
    }

    /**
     * Находит подписчика по email.
     *
     * @param email email пользователя
     * @return DTO подписчика или null, если не найден
     */

    public Optional<DiscountSubscriberDto> findByEmail(String email) {
        return repo.findByEmail(email)
                .map(mapper::toDto);
    }

    /**
     * Возвращает общее количество подписчиков на скидки.
     *
     * @return количество подписчиков
     */
    public long countAllSubscribers() {
        return repo.countSubscribers();
    }

    /**
     * Добавляет нового подписчика, если email ещё не зарегистрирован.
     *
     * @param email email для подписки
     * @return DTO созданного подписчика
     * @throws IllegalArgumentException если email уже подписан
     */
    @Transactional
    public DiscountSubscriberDto subscribe(String email) {
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Email уже подписан на скидки: " + email);
        }
        var subscriber = mapper.toEntity(email);
        subscriber.setCreated(LocalDateTime.now());
        return mapper.toDto(repo.save(subscriber));
    }
}