package by.language.platform.mapper;

import by.language.platform.dto.CourseDto;
import by.language.platform.model.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseDto toDto(Course course);

    Course toEntity(CourseDto dto);
}