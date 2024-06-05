package org.tum.thesis.membershipmanagementservice.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tum.thesis.membershipmanagementservice.web.dto.AddVerifiableCredentialRequest;
import org.tum.thesis.membershipmanagementservice.web.dto.RevokeVerifiableCredentialRequest;
import org.tum.thesis.membershipmanagementservice.web.dto.UnrevokeVerifiableCredentialRequest;
import org.tum.thesis.membershipmanagementservice.web.service.MembershipService;

import java.io.IOException;
import java.util.Set;

@RestController
public class MembershipController {
    private final MembershipService service;

    @Autowired
    public MembershipController(MembershipService service) {
        this.service = service;
    }
    @PostMapping("/addVerifiableCredential")
    public String addVerifiableCredential(@RequestBody AddVerifiableCredentialRequest addVerifiableCredentialRequest) throws IOException {
        boolean status = service.addVerifiableCredential(addVerifiableCredentialRequest.getVc());
        return "request completed: " + status;
    }

    @PostMapping("/revokeVerifiableCredential")
    public String revokeVerifiableCredential(@RequestBody RevokeVerifiableCredentialRequest revokeVerifiableCredentialRequest) throws IOException {
        boolean status = service.revokeVerifiableCredential(revokeVerifiableCredentialRequest.getVc());
        return "request completed: " + status;
    }

    @PostMapping("/unrevokeVerifiableCredential")
    public String unrevokeVerifiableCredential(@RequestBody UnrevokeVerifiableCredentialRequest unrevokeVerifiableCredentialRequest) throws IOException {
        boolean status = service.unrevokeVerifiableCredential(unrevokeVerifiableCredentialRequest.getVc());
        return "request completed: " + status;
    }

    @GetMapping("/listVerifiableCredentials")
    public Set<String> listVerifiableCredentials() {
        return service.listVerifiableCredentials();
    }

    // TODO: add suspension method.
    // TODO: add documentation for a cron job with a configurable schedule that unrevokes vcs whose suspension period has expired.
}
