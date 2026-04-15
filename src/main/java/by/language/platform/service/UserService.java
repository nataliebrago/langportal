package by.language.platform.service;


import by.language.platform.dto.DiscountSubscriberDto;
import by.language.platform.dto.UserCreateDto;
import by.language.platform.dto.UserDto;
import by.language.platform.exception.EmailBusyException;
import by.language.platform.mapper.UserMapper;
import by.language.platform.model.Role;
import by.language.platform.model.User;
import by.language.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import by.language.platform.exception.UserNotFoundException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    @Transactional
    public UserDto create(UserCreateDto dto) {
        if (repo.existsByEmail(dto.email()))
            throw new EmailBusyException(dto.email());
        User u = mapper.toEntity(dto);
        u.setPassword(encoder.encode(dto.password()));
        u.setRole(Role.USER);
        u.setRegistered(true);
        return mapper.toDto(repo.save(u));
    }

    @Transactional(readOnly = true)
    public Page<UserDto> listConfirmed(Pageable p) {
        return repo.findAllConfirmed(p).map(mapper::toDto);
    }

    @Transactional
    public void changePassword(Long id, String currentRaw, String newRaw) {
        User user = repo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!encoder.matches(currentRaw, user.getPassword())) {
            throw new BadCredentialsException("Неверный текущий пароль");
        }

        if (encoder.matches(newRaw, user.getPassword())) {
            throw new IllegalArgumentException("Новый пароль совпадает со старым");
        }

        user.setPassword(encoder.encode(newRaw));
        repo.save(user);
    }

    /**
     * Проверяет, существует ли пользователь с указанным email.
     *
     * @param email email для проверки
     * @return true, если существует
     */
    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public boolean existsById(Long userId) {
        return repo.existsById(userId);
    }

}