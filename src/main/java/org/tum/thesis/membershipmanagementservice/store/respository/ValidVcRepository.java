package org.tum.thesis.membershipmanagementservice.store.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tum.thesis.membershipmanagementservice.store.model.InvalidVc;
import org.tum.thesis.membershipmanagementservice.store.model.ValidVc;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface ValidVcRepository extends JpaRepository<ValidVc, String> {

    default Set<String> listAllIds() {
        return findAll().stream().map(ValidVc::getId).collect(Collectors.toSet());
    }
}
