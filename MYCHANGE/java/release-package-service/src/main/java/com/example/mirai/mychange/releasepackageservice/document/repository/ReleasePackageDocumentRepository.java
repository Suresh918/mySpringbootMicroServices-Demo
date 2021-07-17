package com.example.mirai.projectname.releasepackageservice.document.repository;


import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageDocument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ReleasePackageDocumentRepository extends JpaRepository<ReleasePackageDocument, Long>,
        JpaSpecificationExecutor<ReleasePackageDocument>,
        BaseRepository<ReleasePackageDocumentRepository> {

    @Query(value = "SELECT COUNT(d.id) FROM ReleasePackageDocument d WHERE release_package_id=?1")
    Integer getCountByReleasePackageId(Long releasePackageId);

    @Query("SELECT COUNT(d.id) FROM ReleasePackageDocument d WHERE release_package_id =?1 and 'OTHER' MEMBER OF tags")
    Integer getOtherDocumentsCount(Long releasePackageId);
}
