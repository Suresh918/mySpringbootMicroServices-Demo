package com.example.mirai.projectname.changerequestservice.migration.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestCommentDocument;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestCommentDocumentService;
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
public class ChangeRequestCommentDocumentMigrateController {
    private final ChangeRequestCommentDocumentService changeRequestCommentDocumentService;

    @PostMapping({"/comments/{parentId}/documents"})
    @ResponseStatus(HttpStatus.CREATED)
    public BaseEntityInterface migrateCommentDocument(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("description") String description,
                                                      @RequestParam("tags") String tags,
                                                      @PathVariable Long parentId) throws IOException {
        ChangeRequestCommentDocument entityIns = new ChangeRequestCommentDocument();
        entityIns.setName(file.getOriginalFilename());
        entityIns.setType(file.getContentType());
        entityIns.setDescription(description);
        entityIns.setSize(file.getSize() / 1024);
        entityIns.setTags(Arrays.asList(tags.split(",")));
        return changeRequestCommentDocumentService.createMigrationDocument(entityIns, parentId, ChangeRequestComment.class, file);
    }
}
