package com.example.FitnessCenterApp.service.membership;

import com.example.FitnessCenterApp.controller.membership.CreateMembershipRequest;
import com.example.FitnessCenterApp.controller.membership.MembershipDto;
import com.example.FitnessCenterApp.mapper.MembershipMapper;
import com.example.FitnessCenterApp.model.MembershipDB;
import com.example.FitnessCenterApp.repository.MembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembershipServiceImplementation implements MembershipService{
    private final MembershipRepository membershipRepository;
    private final MembershipMapper membershipMapper;

    @Autowired
    public MembershipServiceImplementation(MembershipRepository membershipRepository,
                                           MembershipMapper membershipMapper) {
        this.membershipRepository = membershipRepository;
        this.membershipMapper = membershipMapper;
    }

    public MembershipDto saveMembership(CreateMembershipRequest createMembershipRequest) {
        MembershipDB mappedMembership = membershipMapper.toDB(createMembershipRequest);
        MembershipDB savedMembership = membershipRepository.save(mappedMembership);
        return membershipMapper.fromDB(savedMembership);
    }

    public List<MembershipDto> getAllMemberships() {
        return membershipRepository.findAll().stream()
                .map(membershipMapper::fromDB)
                .collect(Collectors.toList());
    }
}
