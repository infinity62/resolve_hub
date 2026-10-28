package com.jsp.resolve_hub.dao;

import com.jsp.resolve_hub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Database access for users. JpaRepository already provides save, findById,
 * findAll, existsById, deleteById and more. Spring builds the two methods
 * below automatically from their names.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);        // used to detect duplicate registration

    Optional<User> findByEmail(String email);   // used by login and by the JWT filter
}