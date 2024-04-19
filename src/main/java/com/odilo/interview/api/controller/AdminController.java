package com.odilo.interview.api.controller;

import com.odilo.interview.api.dto.ChangePasswordRequest;
import com.odilo.interview.api.dto.UserListResponse;
import com.odilo.interview.api.mapper.UserMapper;
import com.odilo.interview.exception.ApiError;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/odilo/api/admin/users")
@Tag(name = "AdminController", description = "The Admin API - Only for admin users")
public class AdminController {

    private final AdminService adminService;
    private final UserMapper userMapper;

    @Operation(summary = "Get all user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserListResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized resource access",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @GetMapping("")
    public ResponseEntity<UserListResponse> getAllUsers() {
        List<UserEntity> userEntityList = adminService.getAllUsers();
        return ResponseEntity.ok(userMapper.userListToUserListResponse(userEntityList));
    }

    @Operation(summary = "Change user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized resource access",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "409", description = "New and actual password are the same",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @PutMapping("/{userId}")
    public ResponseEntity<?> changeUserPassword(@PathVariable Long userId,
                                                @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {

        adminService.changeUserPassword(userId, changePasswordRequest);
        return ResponseEntity.noContent().build();
    }
}
