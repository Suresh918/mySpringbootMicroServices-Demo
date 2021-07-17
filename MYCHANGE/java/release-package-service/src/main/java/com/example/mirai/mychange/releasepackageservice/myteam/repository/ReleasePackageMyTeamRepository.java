package com.example.mirai.projectname.releasepackageservice.myteam.repository;


import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ReleasePackageMyTeamRepository extends JpaRepository<ReleasePackageMyTeam, Long>, JpaSpecificationExecutor<ReleasePackageMyTeam>,
        BaseRepository<ReleasePackageMyTeam> {
    @Query("SELECT myteam.id FROM ReleasePackageMyTeam myteam WHERE myteam.releasePackage.releasePackageNumber=?1")
    Long findReleasePackageMyTeam(String releasePackageId);

}
