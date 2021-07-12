package com.example.mirai.projectname.changerequestservice.customerimpact.repository;

import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.libraries.entity.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerImpactRepository extends JpaRepository<CustomerImpact, Long>,
        JpaSpecificationExecutor<CustomerImpact>, BaseRepository<CustomerImpact> {
}
