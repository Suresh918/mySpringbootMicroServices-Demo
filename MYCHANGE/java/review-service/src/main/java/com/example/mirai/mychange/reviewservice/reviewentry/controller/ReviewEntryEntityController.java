package com.example.mirai.projectname.reviewservice.reviewentry.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.projectname.reviewservice.core.component.EntityResolver;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.service.ReviewEntryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("reviews/{entityType:review-entries}")
public class ReviewEntryEntityController extends EntityController {


    public ReviewEntryEntityController(ObjectMapper objectMapper, ReviewEntryService reviewEntryService, EntityResolver entityResolver) {
        super(objectMapper, reviewEntryService, entityResolver);
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ReviewEntry.class;
    }

    @PutMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    @Override
    public BaseEntityInterface update(@PathVariable Long id, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        Map<String, Object> newInsChangedAttrs = ObjectMapperUtil.getChangedAttributes(jsonNode);
        BaseEntityInterface entityIns = this.objectMapper.treeToValue(jsonNode, this.getEntityClass());
        entityIns.setId(id);
        return ((ReviewEntryService)this.entityServiceDefaultInterface).updateReviewEntry(entityIns, newInsChangedAttrs);
    }

}
