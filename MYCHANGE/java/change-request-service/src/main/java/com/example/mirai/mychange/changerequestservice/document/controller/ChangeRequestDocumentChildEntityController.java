package com.example.mirai.projectname.changerequestservice.document.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.document.controller.DocumentChildEntityController;
import com.example.mirai.libraries.document.model.dto.DocumentCategory;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestDocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("{parentType:change-requests}/{parentId}/{entityType:documents}")
public class ChangeRequestDocumentChildEntityController extends DocumentChildEntityController {

    private final ChangeRequestDocumentService changeRequestDocumentService;
    public ChangeRequestDocumentChildEntityController(ObjectMapper objectMapper,
                                                      ChangeRequestDocumentService changeRequestDocumentService, EntityResolver entityResolver) {
        super(objectMapper, changeRequestDocumentService, entityResolver);
        this.changeRequestDocumentService = changeRequestDocumentService;
    }

    @GetMapping(
            params = {"view=categorized", "tags=all"}
    )
    @ResponseStatus(HttpStatus.OK)
    public List<DocumentCategory> getCategorizedDocuments(@PathVariable String parentType, @PathVariable Long parentId, @PathVariable String entityType, @RequestParam(name = "view") String view, @RequestParam(name = "criteria", defaultValue = "") String criteria, @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria, @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect, @PageableDefault(20) Pageable pageable) {
        Class<? extends BaseEntityInterface> parentEntityClass = entityResolverDefaultInterface.getEntityClass(parentType);
        EntityLink entityLink = new EntityLink(parentId, parentEntityClass);
        return changeRequestDocumentService.getAllCategorizedDocuments(entityLink, criteria, viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }
}
