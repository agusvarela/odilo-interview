package com.odilo.interview.api.controller;

import com.odilo.interview.api.dto.UserResponse;
import com.odilo.interview.api.mapper.UserMapper;
import com.odilo.interview.exception.ApiError;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/odilo/api/users")
@Tag(name = "UserController", description = "The User API - Only for users authenticated")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Get user by userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized resource access",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "403", description = "Userid does not match with the logged user token or access denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId,
                                                    @RequestHeader("Authorization") String authorizationHeader) {

        UserEntity userEntity = userService.getUserById(userId, authorizationHeader);
        return ResponseEntity.ok(userMapper.userToUserResponse(userEntity));
    }

    @Operation(summary = "Delete a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized resource access",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "403", description = "Userid does not match with the logged user token or access denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long userId,
                                            @RequestHeader("Authorization") String authorizationHeader) {

        userService.deleteUserById(userId, authorizationHeader);
        return ResponseEntity.noContent().build();
    }
}
