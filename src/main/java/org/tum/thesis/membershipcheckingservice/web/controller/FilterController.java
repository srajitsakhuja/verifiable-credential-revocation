package org.tum.thesis.membershipcheckingservice.web.controller;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tum.thesis.membershipcheckingservice.web.dto.CheckMembershipRequest;
import org.tum.thesis.membershipcheckingservice.web.dto.GenerateFilterRequest;
import org.tum.thesis.membershipcheckingservice.web.service.GeneratorService;
import org.tum.thesis.membershipcheckingservice.web.service.CheckMembershipService;

@RestController
public class FilterController {
  private final CheckMembershipService checkMembershipService;
  private final GeneratorService generatorService;
  private static final String IPNS_ADDRESS_FOR_TESTING = "k51qzi5uqu5dm580c4rapmtyxgbkm9eebih45wa30g75pr2fpnqxk26zj4tggt";

  @Autowired
  FilterController(CheckMembershipService checkMembershipService, GeneratorService generatorService) {
    this.checkMembershipService = checkMembershipService;
    this.generatorService = generatorService;
  }
  @PostMapping("/generateFilter")
  public String generateFilter(@RequestBody GenerateFilterRequest generateFilterRequest) throws IOException {
    return generatorService.generateFiler(generateFilterRequest.getValidVcs(), generateFilterRequest.getInvalidVcs());

//    Set<VerifiableCredential<?>> validVcs = convert(generateFilterRequest.getValidVcs());
//    Set<VerifiableCredential<?>> invalidVcs = convert(generateFilterRequest.getInvalidVcs());
//
//    CascadingFilter cascadingFilter = new CascadingBloomFilter();
//    cascadingFilter.generate(validVcs, invalidVcs);
//
//    return SerializationUtils.serialize(cascadingFilter);
  }

  @PostMapping("/checkMembership")
  public boolean verify(@RequestBody CheckMembershipRequest checkMembershipRequest) throws IOException, ParseException {
    return checkMembershipService.check(checkMembershipRequest.getIpnsAddress(), checkMembershipRequest.getVcId());
  }

//  @PostMapping("/verify/{vcId}")
//  public boolean verify(@RequestBody byte[] filter, @PathVariable String vcId) throws IOException, ParseException {
//    CascadingFilter cascadingFilter = (CascadingFilter) (SerializationUtils.deserialize(filter));
//    if (cascadingFilter == null) {
//      throw new IllegalArgumentException("Malformed cascading filter provided");
//    }
//
//    return cascadingFilter.isRevoked(new VerifiableCredential<UUID>(UUID.fromString(vcId)));
//  }
}
