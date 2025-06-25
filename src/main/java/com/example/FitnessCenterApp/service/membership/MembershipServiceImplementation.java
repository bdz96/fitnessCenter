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
    @Autowired
    MembershipRepository  membershipRepository;

    public MembershipDto saveMembership (CreateMembershipRequest createMembershipRequest){
        MembershipDB mappedMembership = MembershipMapper.toDB(createMembershipRequest);
        MembershipDB savedMembership = membershipRepository.save(mappedMembership);
        return MembershipMapper.fromDB(savedMembership);
    }

    public List<MembershipDto> getAllMemberships (){
        List<MembershipDB> memberships = membershipRepository.findAll();

        return memberships.stream()
                .map(MembershipMapper::fromDB)
                .collect(Collectors.toList());

    }
}
