package com.panda.tokenbucketcruddemo.controller;

import com.panda.tokenbucketcruddemo.dto.UserRequest;
import com.panda.tokenbucketcruddemo.dto.UserResponse;
import com.panda.tokenbucketcruddemo.entity.User;
import com.panda.tokenbucketcruddemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for User CRUD operations.
 * All endpoints are protected by rate limiting via RateLimitFilter.
 * 
 * Endpoints:
 * - POST /api/users     - Create new user
 * - GET /api/users     - List all users
 * - GET /api/users/{id} - Get user by ID
 * - PUT /api/users/{id} - Update user
 * - DELETE /api/users/{id} - Delete user
 * 
 * Rate limiting: Per-user via X-User-ID header (falls back to IP).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Create a new user.
     * POST /api/users
     * @param request User creation data
     * @return Created user with 201 status
     */
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
        User user = new User(null, request.getName(), request.getEmail());
        User created = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(created));
    }

    /**
     * List all users.
     * GET /api/users
     * @return List of all users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> users = userService.findAll().stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID.
     * GET /api/users/{id}
     * @param id User ID
     * @return User if found, 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(UserResponse.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update existing user.
     * PUT /api/users/{id}
     * @param id User ID to update
     * @param request Updated user data
     * @return Updated user if found, 404 otherwise
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody UserRequest request) {
        User user = new User(null, request.getName(), request.getEmail());
        return userService.update(id, user)
                .map(updated -> ResponseEntity.ok(UserResponse.from(updated)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete user by ID.
     * DELETE /api/users/{id}
     * @param id User ID to delete
     * @return 204 if deleted, 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}