package org.tum.thesis.membershipmanagementservice.store.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tum.thesis.membershipmanagementservice.store.model.InvalidVc;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface InvalidVcRepository extends JpaRepository<InvalidVc, String> {
    default Set<String> listAllIds() {
      return findAll().stream().map(InvalidVc::getId).collect(Collectors.toSet());
    }
}
