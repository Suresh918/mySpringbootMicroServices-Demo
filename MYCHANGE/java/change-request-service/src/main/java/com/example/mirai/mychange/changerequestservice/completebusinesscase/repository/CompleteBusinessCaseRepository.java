package com.example.mirai.projectname.changerequestservice.completebusinesscase.repository;

import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.libraries.entity.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompleteBusinessCaseRepository extends JpaRepository<CompleteBusinessCase, Long>,
        JpaSpecificationExecutor<CompleteBusinessCase>, BaseRepository<CompleteBusinessCase> {
}
