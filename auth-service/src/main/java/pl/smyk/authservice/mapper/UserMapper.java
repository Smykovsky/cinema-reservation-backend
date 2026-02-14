package pl.smyk.authservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.smyk.authservice.model.User;
import pl.smyk.common.dto.UserDto;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(role -> role.name()).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "permissions", expression = "java(user.getRoles().stream().flatMap(role -> role.getPermissions().stream()).map(permission -> permission.name()).distinct().sorted().collect(java.util.stream.Collectors.toList()))")
    @Mapping(source = "totpEnabled", target = "totpEnabled")
    UserDto userToUserDto(User user);

}