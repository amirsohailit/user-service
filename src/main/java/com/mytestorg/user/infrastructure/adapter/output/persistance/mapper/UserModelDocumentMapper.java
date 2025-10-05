package com.mytestorg.user.infrastructure.adapter.output.persistance.mapper;

import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface UserModelDocumentMapper {
    @Mapping(target = "createdAt", expression = "java(toOffsetDateTime(userDocument.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(toOffsetDateTime(userDocument.getUpdatedAt()))")
    UserModel userDocumentToUserModel(final UserDocument userDocument);

    @Mapping(target = "createdAt", expression = "java(toInstant(userModel.createdAt()))")
    @Mapping(target = "updatedAt", expression = "java(toInstant(userModel.updatedAt()))")
    UserDocument userModelToUserDocument(final UserModel userModel);

    default OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? instant.atZone(ZoneId.systemDefault()).toOffsetDateTime() : null;
    }

    default Instant toInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toInstant() : null;
    }

}
