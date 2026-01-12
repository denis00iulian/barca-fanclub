package com.barca.fanclub_api.service;

import com.barca.fanclub_api.dto.ActivateMembershipResponse;
import com.barca.fanclub_api.dto.MembershipResponse;
import com.barca.fanclub_api.exception.ResourceNotFoundException;
import com.barca.fanclub_api.model.Membership;
import com.barca.fanclub_api.model.MembershipStatus;
import com.barca.fanclub_api.repository.MembershipRepository;
import com.barca.fanclub_api.model.User;
import com.barca.fanclub_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public MembershipServiceImpl(MembershipRepository membershipRepository, UserRepository userRepository) {
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.clock = Clock.systemUTC();
    }

    @Override
    @Transactional
    public ActivateMembershipResponse activateMembership(UUID userId, int year) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Membership membership = membershipRepository.findMembershipByUserAndYear(userId, year)
                .orElse(new Membership(user, year));

        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setStartsAt(Instant.now(clock));
        membership.setEndsAt(Instant.parse(year + "-12-31T23:59:59Z")); //31 dec of current year

        membershipRepository.save(membership);

        return new ActivateMembershipResponse(
                userId,
                year,
                membership.getStatus(),
                membership.getStartsAt(),
                membership.getEndsAt()
        );
    }

    @Override
    public MembershipResponse getUserCurrentMembership(UUID userId) {
        int year = Year.now(ZoneOffset.UTC).getValue();
        Membership membership = membershipRepository.findMembershipByUserAndYear(userId, year)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        return new MembershipResponse(
                membership.getUser().getId(),
                membership.getYear(),
                membership.getStatus(),
                membership.getStartsAt(),
                membership.getEndsAt()
        );
    }
}
