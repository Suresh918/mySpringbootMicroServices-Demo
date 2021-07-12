package com.example.mirai.projectname.changerequestservice.solutiondefinition.repository;

import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import com.example.mirai.libraries.entity.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolutionDefinitionRepository extends JpaRepository<SolutionDefinition, Long>,
        JpaSpecificationExecutor<SolutionDefinition>, BaseRepository<SolutionDefinition> {
}
