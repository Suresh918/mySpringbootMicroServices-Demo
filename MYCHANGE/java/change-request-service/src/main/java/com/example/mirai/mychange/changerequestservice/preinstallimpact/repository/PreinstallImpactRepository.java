package com.example.mirai.projectname.changerequestservice.preinstallimpact.repository;

import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PreinstallImpactRepository extends JpaRepository<PreinstallImpact, Long>,
        JpaSpecificationExecutor<PreinstallImpact>, BaseRepository<PreinstallImpact> {
    PreinstallImpact findByImpactAnalysisId(Long impactAnalysisId);
}
