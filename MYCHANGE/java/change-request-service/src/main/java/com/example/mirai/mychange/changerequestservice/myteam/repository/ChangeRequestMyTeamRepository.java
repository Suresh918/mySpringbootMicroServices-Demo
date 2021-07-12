package com.example.mirai.projectname.changerequestservice.myteam.repository;

import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChangeRequestMyTeamRepository extends JpaRepository<ChangeRequestMyTeam, Long>, JpaSpecificationExecutor<ChangeRequestMyTeam>,
        BaseRepository<ChangeRequestMyTeam> {
}
