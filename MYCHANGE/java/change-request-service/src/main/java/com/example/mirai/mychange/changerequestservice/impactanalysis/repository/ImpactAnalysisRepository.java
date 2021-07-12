package com.example.mirai.projectname.changerequestservice.impactanalysis.repository;

import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.libraries.entity.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImpactAnalysisRepository extends JpaRepository<ImpactAnalysis, Long>,
        JpaSpecificationExecutor<ImpactAnalysis>, BaseRepository<ImpactAnalysis> {

}
