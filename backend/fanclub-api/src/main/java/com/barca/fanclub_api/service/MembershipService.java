package com.barca.fanclub_api.service;

import com.barca.fanclub_api.dto.ActivateMembershipResponse;
import com.barca.fanclub_api.dto.MembershipResponse;

import java.util.UUID;

public interface MembershipService {

    ActivateMembershipResponse activateMembership(UUID userId, int year);

    MembershipResponse getUserCurrentMembership(UUID userId);

}
