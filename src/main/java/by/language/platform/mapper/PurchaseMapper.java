package by.language.platform.mapper;

import by.language.platform.dto.PurchaseDto;
import by.language.platform.model.Purchase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CourseMapper.class})
public interface PurchaseMapper {
    PurchaseDto toDto(Purchase purchase);
}