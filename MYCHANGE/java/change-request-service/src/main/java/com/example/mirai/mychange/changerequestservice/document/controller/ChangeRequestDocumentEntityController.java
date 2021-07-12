package com.example.mirai.projectname.changerequestservice.document.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.document.controller.DocumentEntityController;
import com.example.mirai.libraries.document.model.dto.DocumentCategory;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestDocument;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestDocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({"{parentType:change-requests}/{entityType:documents}", "{parentType:change-requests}/{parentId}/{entityType:documents}"})
public class ChangeRequestDocumentEntityController extends DocumentEntityController {

    private ChangeRequestDocumentService changeRequestDocumentService;
    public ChangeRequestDocumentEntityController(ObjectMapper objectMapper, ChangeRequestDocumentService changeRequestDocumentService,
                                                 EntityResolver entityResolver) {
        super(objectMapper, changeRequestDocumentService, entityResolver);
        this.changeRequestDocumentService = changeRequestDocumentService;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ChangeRequestDocument.class;
    }




    @GetMapping(params = {"agenda-item-id","view=categorized", "tags=all"})
    @ResponseStatus(HttpStatus.OK)
    public List<DocumentCategory> getDocumentsOverviewByAgendaItemId(@RequestParam(name="agenda-item-id") String agendaItemId,
                                                                     @RequestParam(name = "criteria",defaultValue = "") String criteria,
                                                                     @RequestParam(name = "view-criteria",defaultValue = "") String viewCriteria,
                                                                     @RequestParam(name = "slice-select",defaultValue = "") String sliceSelect,
                                                                     @PageableDefault(20) Pageable pageable) {
        return changeRequestDocumentService.getAllDocumentsOverviewByAgendaItemId(agendaItemId, criteria, viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

}
