package com.server.youthtalktalk.domain.policy.entity.region;

import com.server.youthtalktalk.domain.policy.entity.Policy;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PolicySubRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name =  "policy_sub_region_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_region_id")
    private SubRegion subRegion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private Policy policy;

    /** 연관관계 메서드 */
    public void setPolicy(Policy policy) {
        this.policy = policy;
        if(policy != null) {
            policy.getPolicySubRegions().add(this);
        }
    }

    /** 연관관계 메서드 */
    public void setSubRegion(SubRegion subRegion) {
        this.subRegion = subRegion;
        if(subRegion != null) {
            subRegion.getPolicySubRegions().add(this);
        }
    }
}
