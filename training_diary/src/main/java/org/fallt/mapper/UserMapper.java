package org.fallt.mapper;

import org.fallt.dto.request.RegisterRq;
import org.fallt.dto.response.LoginRs;
import org.fallt.dto.response.RegisterRs;
import org.fallt.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registration", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "role", expression = "java(org.fallt.model.Role.ROLE_USER)")
    User dtoToEntity(RegisterRq request);

    @Mapping(target = "timestamp", expression = "java(System.currentTimeMillis())")
    RegisterRs toRegisterResponse(User user);

    @Mapping(target = "timestamp", expression ="java(System.currentTimeMillis())")
    @Mapping(target = "success", expression = "java(true)")
    LoginRs toLoginResponse(User user);
}
