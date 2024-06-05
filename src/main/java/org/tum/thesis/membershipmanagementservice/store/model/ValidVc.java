package org.tum.thesis.membershipmanagementservice.store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ValidVc {
    @Id
    private String id;

    @Column(length=5000)
    private String vc;
}
