package com.panda.tokenbucketcruddemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity representing a user in the system.
 * Uses in-memory storage (ConcurrentHashMap) for demo purposes.
 * In production, this would be a JPA entity mapped to a database table.
 * 
 * Fields:
 * - id: Unique identifier (auto-generated)
 * - name: User's full name
 * - email: User's email address
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}