package com.school.attendance.repository;

import com.school.attendance.entity.User;
import com.school.attendance.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by username or email
     */
    @Query("SELECT u FROM User u WHERE u.username = :login OR u.email = :login")
    Optional<User> findByUsernameOrEmail(@Param("login") String login);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all users by role
     */
    List<User> findByRole(Role role);
    
    /**
     * Find all active users
     */
    List<User> findByIsActiveTrue();
    
    /**
     * Find active users by role
     */
    List<User> findByRoleAndIsActiveTrue(Role role);
    
    /**
     * Find user by reference ID and role (for linking to Student/Teacher)
     */
    Optional<User> findByReferenceIdAndRole(Long referenceId, Role role);
    
    /**
     * Count users by role
     */
    long countByRole(Role role);
    
    /**
     * Count active users by role
     */
    long countByRoleAndIsActiveTrue(Role role);
}
