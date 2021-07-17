package com.example.mirai.projectname.releasepackageservice.document.controller;

import java.util.List;
import java.util.Optional;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.document.model.dto.DocumentCategory;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageDocumentService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import lombok.Data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:release-packages}")
@Data
public class ReleasePackageDocumentDtoController {

    private final EntityResolver entityResolver;
    private final ReleasePackageDocumentService releasePackageDocumentService;
    private final ReleasePackageService releasePackageService;

    @GetMapping(value = "/{releasePackageId:[0-9]+}/{entityType:documents}", params = {"view=categorized", "tags=all"})
    @ResponseStatus(HttpStatus.OK)
    public List<DocumentCategory> getCategorizedDocumentsById(@PathVariable String parentType, @PathVariable Long releasePackageId, @PathVariable String entityType, @RequestParam(name = "view") String view, @RequestParam(name = "criteria", defaultValue = "") String criteria, @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria, @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect, @PageableDefault(20) Pageable pageable) {
        Class<? extends BaseEntityInterface> parentEntityClass = entityResolver.getEntityClass(parentType);
        EntityLink entityLink = new EntityLink(releasePackageId, parentEntityClass);
        return releasePackageDocumentService.getAllCategorizedDocuments(entityLink, criteria, viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(value = "/{releasePackageNumber:[0-9]+-[0-9]+}/{entityType:documents}", params = {"view=categorized", "tags=all"})
    @ResponseStatus(HttpStatus.OK)
    public List<DocumentCategory> getCategorizedDocumentsByReleasePackageNumber(@PathVariable String parentType, @PathVariable String releasePackageNumber, @PathVariable String entityType, @RequestParam(name = "view") String view, @RequestParam(name = "criteria", defaultValue = "") String criteria, @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria, @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect, @PageableDefault(20) Pageable pageable) {
        Class<? extends BaseEntityInterface> parentEntityClass = entityResolver.getEntityClass(parentType);
        Long releasePackageId = releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber);
        EntityLink entityLink = new EntityLink(releasePackageId, parentEntityClass);
        return releasePackageDocumentService.getAllCategorizedDocuments(entityLink, criteria, viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(value = "/{ecn:^ECN-[0-9]+}/{entityType:documents}", params = {"view=categorized", "tags=all"})
    @ResponseStatus(HttpStatus.OK)
    public List<DocumentCategory> getCategorizedDocumentsByEcn(@PathVariable String parentType, @PathVariable String ecn, @PathVariable String entityType, @RequestParam(name = "view") String view, @RequestParam(name = "criteria", defaultValue = "") String criteria, @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria, @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect, @PageableDefault(20) Pageable pageable) {
        Class<? extends BaseEntityInterface> parentEntityClass = entityResolver.getEntityClass(parentType);
        Long releasePackageId = releasePackageService.getReleasePackageIdByContext(ecn, "ECN");
        EntityLink entityLink = new EntityLink(releasePackageId, parentEntityClass);
        return releasePackageDocumentService.getAllCategorizedDocuments(entityLink, criteria, viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }
}
