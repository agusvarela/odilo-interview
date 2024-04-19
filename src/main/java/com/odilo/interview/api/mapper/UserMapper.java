package com.odilo.interview.api.mapper;

import com.odilo.interview.api.dto.UserListResponse;
import com.odilo.interview.api.dto.UserResponse;
import com.odilo.interview.model.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse userToUserResponse(UserEntity userEntity);

    List<UserResponse> userListToUserResponseList(List<UserEntity> userEntities);

    default UserListResponse userListToUserListResponse(List<UserEntity> userEntities) {
        return UserListResponse.builder()
                .users(userListToUserResponseList(userEntities))
                .build();
    }

}
