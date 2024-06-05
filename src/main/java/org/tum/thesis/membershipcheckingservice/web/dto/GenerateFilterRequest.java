package org.tum.thesis.membershipcheckingservice.web.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class GenerateFilterRequest {
  List<UUID> validVcs;
  List<UUID> invalidVcs;
}
