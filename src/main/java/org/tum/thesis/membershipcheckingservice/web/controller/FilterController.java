package org.tum.thesis.membershipcheckingservice.web.controller;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tum.thesis.membershipcheckingservice.web.dto.CheckMembershipRequest;
import org.tum.thesis.membershipcheckingservice.web.service.CheckMembershipService;

@RestController
public class FilterController {
  private final CheckMembershipService checkMembershipService;

  @Autowired
  FilterController(CheckMembershipService checkMembershipService) {
    this.checkMembershipService = checkMembershipService;
  }

  @PostMapping("/checkMembership")
  public boolean verify(@RequestBody CheckMembershipRequest checkMembershipRequest) throws IOException, ParseException {
    return checkMembershipService.check(checkMembershipRequest.getIpnsAddress(), checkMembershipRequest.getVcId());
  }
}
