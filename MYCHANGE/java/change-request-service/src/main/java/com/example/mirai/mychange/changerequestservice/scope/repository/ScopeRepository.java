package com.example.mirai.projectname.changerequestservice.scope.repository;

import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ScopeRepository extends JpaRepository<Scope, Long>,
        JpaSpecificationExecutor<Scope>, BaseRepository<Scope> {
}
