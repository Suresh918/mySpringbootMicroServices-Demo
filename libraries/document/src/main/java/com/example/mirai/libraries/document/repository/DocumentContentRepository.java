package com.example.mirai.libraries.document.repository;

import com.example.mirai.libraries.document.model.DocumentContent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentContentRepository extends JpaRepository<DocumentContent, Long>, JpaSpecificationExecutor<DocumentContent> {
}
