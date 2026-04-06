package by.language.platform.mapper;

import by.language.platform.dto.DiscountSubscriberDto;
import by.language.platform.model.DiscountSubscriber;
import org.springframework.stereotype.Component;

@Component
public class DiscountSubscriberMapper {

    /**
     * Преобразует email в сущность DiscountSubscriber.
     *
     * Используется при создании новой подписки.
     *
     * @param email email пользователя
     * @return новая сущность подписчика без ID и даты (устанавливается отдельно)
     */
    public DiscountSubscriber toEntity(String email) {
        return new DiscountSubscriber(email);
    }

    /**
     * Преобразует сущность в DTO для передачи клиенту.
     *
     * @param entity сущность из БД
     * @return DTO с email и датой подписки
     */
    public DiscountSubscriberDto toDto(DiscountSubscriber entity) {
        if (entity == null) return null;
        return new DiscountSubscriberDto(
                entity.getId(),       // добавлено: ID
                entity.getEmail(),
                entity.getCreated()
        );
    }
}