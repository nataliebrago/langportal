package by.language.platform.service;


import by.language.platform.dto.UserCreateDto;
import by.language.platform.dto.UserDto;
import by.language.platform.exception.EmailBusyException;
import by.language.platform.mapper.UserMapper;
import by.language.platform.model.User;
import by.language.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import by.language.platform.exception.UserNotFoundException;
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    @Transactional
    public UserDto create(UserCreateDto dto) {
        if (repo.existsByEmail(dto.email())) throw new EmailBusyException();
        User u = mapper.toEntity(dto);
        u.setPassword(encoder.encode(dto.password()));
        u.setRegistered(true);
        return mapper.toDto(repo.save(u));
    }

    @Transactional(readOnly = true)
    public Page<UserDto> listConfirmed(Pageable p) {
        return repo.findAllConfirmed(p).map(mapper::toDto);
    }

    @Transactional
    public void changePassword(Long id, String raw) {
        int rows = repo.updatePassword(id, encoder.encode(raw), java.time.LocalDateTime.now());
        if (rows == 0) throw new UserNotFoundException(id);
    }
}