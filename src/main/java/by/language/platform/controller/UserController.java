package by.language.platform.controller;

import by.language.platform.dto.PageDto;
import by.language.platform.dto.PasswordChangeDto;
import by.language.platform.dto.UserCreateDto;
import by.language.platform.dto.UserDto;
import by.language.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import by.language.platform.exception.UserNotFoundException;
import by.language.platform.exception.EmailBusyException;


@RestController
@RequestMapping("/users")
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
    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные (например, пустой email)"),
            @ApiResponse(responseCode = "409", description = "Email уже используется")})
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserCreateDto dto) {
        UserDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Эндпоинт для получения страницы подтверждённых пользователей.
     * <p>
     * Поддерживает пагинацию. По умолчанию возвращается страница размером 20 записей.
     * Использует {@link PageableDefault} для задания параметров по умолчанию.
     * <p>
     * Результат оборачивается в {@link PageDto} для стандартизированного формата ответа.
     *
     * @param pageable объект пагинации (номер страницы, размер, сортировка), автоматически извлекаемый из параметров запроса
     * @return страница пользователей в формате {@link PageDto}
     */
    @GetMapping("/confirmed")
    @Operation(summary = "Получение страницы подтверждённых пользователей")
    public PageDto<UserDto> list(@PageableDefault(size = 20) Pageable pageable) {
        return PageDto.of(service.listConfirmed(pageable));
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
    @Operation(summary = "Изменение пароля пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пароль успешно изменён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные (пустой пароль, слишком короткий)"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Новый пароль совпадает со старым (опционально)")
    })
    public  ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody @Valid PasswordChangeDto dto) {
        service.changePassword(id, dto.getCurrentPassword(), dto.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}