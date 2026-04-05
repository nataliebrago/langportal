package by.language.platform.mapper;

import by.language.platform.dto.UserCreateDto;
import by.language.platform.dto.UserDto;
import by.language.platform.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserCreateDto dto);
}