package com.barca.fanclub_api.repository;

import com.barca.fanclub_api.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM memberships m
            WHERE m.user_id = :userId
              AND m.status = 'ACTIVE'
              AND m.starts_at <= :now
              AND m.ends_at >= :now
        )
        """, nativeQuery = true)
    boolean userHasActiveMembershipAt(UUID userId, Instant now);

    @Query(value = """
        SELECT *
        FROM memberships m
        WHERE m.user_id = :userId
            AND m.year = :year
        LIMIT 1
        """, nativeQuery = true)
    Optional<Membership> findMembershipByUserAndYear(UUID userId, int year);

}
