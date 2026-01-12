package com.barca.fanclub_api.repository;

import com.barca.fanclub_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query(value = """
        SELECT *
        FROM users u
        WHERE u.email = :email
        LIMIT 1
        """, nativeQuery = true)
    Optional<User> findUserByEmail(String email);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM users u
            WHERE u.email = :email
        )
        """, nativeQuery = true)
    boolean emailExists(String email);

}
