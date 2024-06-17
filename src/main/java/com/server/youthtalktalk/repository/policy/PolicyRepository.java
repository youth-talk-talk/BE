package com.server.youthtalktalk.repository.policy;

import com.server.youthtalktalk.domain.policy.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends JpaRepository<Policy,String> {

}
