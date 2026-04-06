package by.language.platform.controller;

import by.language.platform.dto.PageDto;
import by.language.platform.dto.PasswordChangeDto;
import by.language.platform.dto.UserCreateDto;
import by.language.platform.dto.UserDto;
import by.language.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import by.language.platform.exception.UserNotFoundException;
import by.language.platform.exception.EmailBusyException;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    /**
     * Эндпоинт для создания нового пользователя.
     * <p>
     * Принимает данные в формате JSON, валидирует их и передаёт на обработку в {@link UserService}.
     * При успешном создании возвращает объект {@link UserDto} и статус {@code 201 Created}.
     *
     * @param dto объект с данными для создания пользователя (email, пароль и т.д.)
     * @return созданный пользователь в формате {@link UserDto}
     * @throws EmailBusyException если указанный email уже занят
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserCreateDto dto) {
        return service.create(dto);
    }

    /**
     * Эндпоинт для получения страницы подтверждённых пользователей.
     * <p>
     * Поддерживает пагинацию. По умолчанию возвращается страница размером 20 записей.
     * Использует {@link PageableDefault} для задания параметров по умолчанию.
     * <p>
     * Результат оборачивается в {@link PageDto} для стандартизированного формата ответа.
     *
     * @param p объект пагинации (номер страницы, размер, сортировка), автоматически извлекаемый из параметров запроса
     * @return страница пользователей в формате {@link PageDto}
     */
    @GetMapping("/confirmed")
    public PageDto<UserDto> list(@PageableDefault(size = 20) Pageable p) {
        return PageDto.of(service.listConfirmed(p));
    }

    /**
     * Эндпоинт для изменения пароля пользователя.
     * <p>
     * Принимает ID пользователя в пути и новый пароль в теле запроса в виде {@link PasswordChangeDto}.
     * Передаёт данные в {@link UserService} для обработки.
     * <p>
     * При успешном обновлении возвращает статус {@code 204 No Content}.
     *
     * @param id  идентификатор пользователя, чей пароль нужно изменить
     * @param dto объект, содержащий новый пароль
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    @PatchMapping("/{id}/password")
    public void changePassword(@PathVariable Long id, @RequestBody PasswordChangeDto dto) {
        service.changePassword(id, dto.getNewPassword());
    }
}