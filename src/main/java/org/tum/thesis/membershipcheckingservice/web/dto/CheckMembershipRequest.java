package org.tum.thesis.membershipcheckingservice.web.dto;

import lombok.Data;

@Data
public class CheckMembershipRequest {
  String ipnsAddress;
  String vcId;
}
