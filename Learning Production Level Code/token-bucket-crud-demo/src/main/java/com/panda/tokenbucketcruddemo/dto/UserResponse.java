package com.panda.tokenbucketcruddemo.dto;

import com.panda.tokenbucketcruddemo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for User responses.
 * Excludes sensitive data and provides a stable API contract.
 * Includes static factory method for conversion from entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;

    /**
     * Converts User entity to UserResponse DTO.
     * @param user the User entity to convert
     * @return UserResponse DTO
     */
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}