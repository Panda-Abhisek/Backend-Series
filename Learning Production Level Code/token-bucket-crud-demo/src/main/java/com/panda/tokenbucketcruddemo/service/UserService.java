package com.panda.tokenbucketcruddemo.service;

import com.panda.tokenbucketcruddemo.entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User service providing CRUD operations.
 * Uses in-memory ConcurrentHashMap for storage (demo purposes).
 * Thread-safe with AtomicLong for ID generation.
 * 
 * In production: replace with @Repository + JPA/Hibernate
 * for persistent storage (MySQL, PostgreSQL, etc.)
 */
@Service
public class UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Creates a new user with auto-generated ID.
     * @param user User entity (id should be null)
     * @return Created user with assigned ID
     */
    public User create(User user) {
        Long id = idGenerator.getAndIncrement();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    /**
     * Finds user by ID.
     * @param id User ID to search for
     * @return Optional containing user if found
     */
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Returns all users.
     * @return List of all users (copy to prevent external modification)
     */
    public java.util.List<User> findAll() {
        return new java.util.ArrayList<>(users.values());
    }

    /**
     * Updates existing user by ID.
     * @param id User ID to update
     * @param user New user data (id will be set from path)
     * @return Optional with updated user if found
     */
    public Optional<User> update(Long id, User user) {
        if (users.containsKey(id)) {
            user.setId(id);
            users.put(id, user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Deletes user by ID.
     * @param id User ID to delete
     * @return true if user was deleted, false if not found
     */
    public boolean delete(Long id) {
        return users.remove(id) != null;
    }
}