package com.example.mirai.projectname.changerequestservice.comment.controller;

import com.example.mirai.libraries.comment.controller.CommentEntityController;
import com.example.mirai.libraries.comment.model.dto.CommentOverview;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.comment.service.ChangeRequestCommentService;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("change-requests/{entityType:comments}")
public class ChangeRequestCommentEntityController extends CommentEntityController {
    private final ChangeRequestCommentService changeRequestCommentService;
    public ChangeRequestCommentEntityController(ObjectMapper objectMapper,
                                                ChangeRequestCommentService changeRequestCommentService,
                                                EntityResolver entityResolver) {
        super(objectMapper, changeRequestCommentService, entityResolver);
        this.changeRequestCommentService = changeRequestCommentService;
    }

    @Override
    public Class<ChangeRequestComment> getEntityClass() {
        return ChangeRequestComment.class;
    }

    @GetMapping(params = {"view=overview","agenda-item-id"})
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<CommentOverview> getChangeRequestCommentOverviewByAgendaItemId(@RequestParam(name = "agenda-item-id") String agendaItemId,
                                                                                         @RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                                                         @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                                                         @PageableDefault(20) Pageable pageable) {
        return changeRequestCommentService.getChangeRequestCommentOverviewByAgendaItemId(agendaItemId, criteria, pageable, Optional.ofNullable(sliceSelect));
    }

}
