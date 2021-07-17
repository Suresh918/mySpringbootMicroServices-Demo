package com.example.mirai.projectname.releasepackageservice.releasepackage.repository;


import java.util.List;

import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ReleasePackageRepository extends JpaRepository<com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage, Long>,
        JpaSpecificationExecutor<com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage>, BaseRepository<ReleasePackage> {

    @Query(value = "SELECT rpc.contextId FROM ReleasePackage rp JOIN rp.contexts rpc where rp.releasePackageNumber=?1 AND rpc.type=?2")
    public Long getContextIdByType(String releasePackageId, String type);

    @Query(value = "SELECT rpc.status FROM ReleasePackage rp JOIN rp.contexts rpc where rp.id=?1 and rpc.type='REVIEW'")
    public String getReviewStatusById(Long id);

    @Query(value = "SELECT nextval('release_package_ecn_seq')", nativeQuery = true)
    public Long getSequenceNumberForEcn();

    @Query(value = "SELECT rp.id FROM ReleasePackage rp JOIN rp.prerequisiteReleasePackages prp where prp.releasePackageId=?1 and rp.id<>?2")
    public List<String> getParentReleasePackageIdsOfPrerequisite(Long releasePackageId, Long omitParent);

    @Query(value = "SELECT rp.id FROM ReleasePackage rp JOIN rp.contexts rpc where rpc.contextId=?1 and rp.status not in ('5','6') and rp.id=rpc.id and rpc.type='CHANGENOTICE'")
    public List<Long> getReleasePackageIdsOfCNContext(String contextId);

}
