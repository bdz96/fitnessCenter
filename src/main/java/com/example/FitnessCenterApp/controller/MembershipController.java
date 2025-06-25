package com.example.FitnessCenterApp.controller;

import com.example.FitnessCenterApp.controller.membership.CreateMembershipRequest;
import com.example.FitnessCenterApp.controller.membership.MembershipDto;
import com.example.FitnessCenterApp.service.membership.MembershipService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MembershipController {
    @Autowired
    MembershipService membershipService;

    @PostMapping(value = "/memberships")
    public ResponseEntity<MembershipDto> addMembership(@RequestBody CreateMembershipRequest createMembershipRequest) {
        try {
            MembershipDto newMembership = membershipService.saveMembership(createMembershipRequest);
            return new ResponseEntity<>(newMembership, HttpStatus.CREATED);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/memberships")
    public ResponseEntity<List<MembershipDto>> getMemberships() {
        List<MembershipDto> membershipList = membershipService.getAllMemberships();
        return new ResponseEntity<>(membershipList, HttpStatus.OK);

    }
}
