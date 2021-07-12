package com.example.mirai.projectname.changerequestservice.migration.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestDocument;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestDocumentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/migrate/change-requests")
public class ChangeRequestDocumentMigrateController {
    private final ChangeRequestDocumentService changeRequestDocumentService;

    @PostMapping({"/{parentId}/documents"})
    @ResponseStatus(HttpStatus.CREATED)
    public BaseEntityInterface createMigrationDocument(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("description") String description,
                                                       @RequestParam("tags") String tags,
                                                       @PathVariable Long parentId) throws IOException {
        ChangeRequestDocument entityIns = new ChangeRequestDocument();
        entityIns.setName(file.getOriginalFilename());
        entityIns.setType(file.getContentType());
        entityIns.setSize(file.getSize()/1024);
        entityIns.setDescription(description);
        entityIns.setTags(Arrays.asList(tags.split(",")));
        return changeRequestDocumentService.createMigrationDocument(entityIns,parentId, ChangeRequest.class, file);
    }

}
