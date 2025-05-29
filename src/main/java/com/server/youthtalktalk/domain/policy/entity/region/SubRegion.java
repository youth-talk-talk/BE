package com.server.youthtalktalk.domain.policy.entity.region;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SubRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_region_id")
    private Long id;

    private String name;

    private String code;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Builder.Default
    @OneToMany(mappedBy = "subRegion")
    private List<PolicySubRegion> policySubRegions = new ArrayList<>();
}
