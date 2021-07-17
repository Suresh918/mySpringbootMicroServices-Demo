package com.example.mirai.projectname.releasepackageservice.migration.controller;

import java.io.IOException;
import java.util.Arrays;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageCommentDocument;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageCommentDocumentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/migrate/release-packages")
public class ReleasePackageCommentDocumentMigrateController {
    private final ReleasePackageCommentDocumentService releasePackageCommentDocumentService;

    @PostMapping({"/comments/{parentId}/documents"})
    @ResponseStatus(HttpStatus.CREATED)
    public BaseEntityInterface migrateCommentDocument(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("description") String description,
                                                      @RequestParam("tags") String tags,
                                                      @PathVariable Long parentId) throws IOException {
        ReleasePackageCommentDocument entityIns = new ReleasePackageCommentDocument();
        entityIns.setName(file.getOriginalFilename());
        entityIns.setType(file.getContentType());
        entityIns.setDescription(description);
        entityIns.setSize(file.getSize() / 1024);
        entityIns.setTags(Arrays.asList(tags.split(",")));
        return releasePackageCommentDocumentService.createMigrationDocument(entityIns, parentId, ReleasePackageComment.class, file);
    }
}
