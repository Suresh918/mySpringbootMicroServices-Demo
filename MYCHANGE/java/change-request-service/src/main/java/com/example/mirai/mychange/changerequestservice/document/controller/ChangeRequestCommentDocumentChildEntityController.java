package com.example.mirai.projectname.changerequestservice.document.controller;


import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.document.controller.DocumentChildEntityController;
import com.example.mirai.libraries.document.model.dto.DocumentCategory;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestCommentDocumentService;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("{parentType:comments}/{parentId}/{entityType:documents}")
public class ChangeRequestCommentDocumentChildEntityController extends DocumentChildEntityController {

    public ChangeRequestCommentDocumentChildEntityController(ObjectMapper objectMapper,
                                                             ChangeRequestCommentDocumentService changeRequestCommentDocumentService,
                                                             EntityResolver entityResolver) {
        super(objectMapper, changeRequestCommentDocumentService, entityResolver);
    }
}
