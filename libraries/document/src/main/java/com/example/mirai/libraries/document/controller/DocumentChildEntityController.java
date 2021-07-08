package com.example.mirai.libraries.document.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.document.model.DocumentDTO;
import com.example.mirai.libraries.document.model.dto.DocumentCategory;
import com.example.mirai.libraries.document.model.dto.DocumentOverview;
import com.example.mirai.libraries.document.service.DocumentService;
import com.example.mirai.libraries.entity.controller.ChildEntityController;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.example.mirai.libraries.util.ReflectionUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public abstract class DocumentChildEntityController extends ChildEntityController {

	public DocumentChildEntityController(ObjectMapper objectMapper,
			DocumentService documentService, EntityResolverDefaultInterface entityResolverDefaultInterface) {
		super(objectMapper, documentService, entityResolverDefaultInterface);
	}

	DocumentService getService() {
		return ((DocumentService) (super.entityServiceDefaultInterface));
	}

	@PostMapping("/na")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public BaseEntityInterface createChildWithLink(@RequestBody JsonNode jsonNode,
			@PathVariable String parentType,
			@PathVariable String entityType,
			@PathVariable Long parentId) {
		return null;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BaseEntityInterface create(@PathVariable String parentType,
			@PathVariable Long parentId,
			@PathVariable String entityType,
			@RequestParam("file") MultipartFile file,
			@RequestParam("description") String description,
			@RequestParam("tags") String tags) {
		Class<? extends BaseEntityInterface> parentEntityClass = this.entityResolverDefaultInterface.getEntityClass(parentType);
		Class<? extends BaseEntityInterface> parentEntityDocumentClass = this.entityResolverDefaultInterface.getEntityClass(parentType, entityType);
		Document document = (Document) ReflectionUtil.createInstance(parentEntityDocumentClass);
		document.setName(file.getOriginalFilename());
		document.setType(file.getContentType());
		document.setSize(file.getSize() / 1024);
		document.setDescription(description);
		document.setTags(Arrays.asList(tags.split(",")));
		return getService().createDocument(document, parentId, parentEntityClass, file);
	}

	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public DocumentDTO updateDocumentWithTag(@PathVariable String parentType,
			@PathVariable Long parentId,
			@PathVariable String entityType,
			@RequestParam("file") MultipartFile file,
			@RequestParam("description") String description,
			@RequestParam("tag") String tag) {
		Class<? extends BaseEntityInterface> parentEntityClass = this.entityResolverDefaultInterface.getEntityClass(parentType);
		Class<? extends BaseEntityInterface> parentEntityDocumentClass = this.entityResolverDefaultInterface.getEntityClass(parentType, entityType);
		Document document = (Document) ReflectionUtil.createInstance(parentEntityDocumentClass);
		document.setName(file.getOriginalFilename());
		document.setType(file.getContentType());
		document.setSize(file.getSize() / 1024);
		document.setDescription(description);
		document.setTags(new ArrayList<>(Arrays.asList(tag)));
		return getService().updateDocumentWithTag(document, parentId, tag, parentEntityClass, file);
	}


	@GetMapping(params = { "view=categorized" })
	@ResponseStatus(HttpStatus.OK)
	public List<DocumentCategory> getCategorizedDocuments(@PathVariable String parentType,
			@PathVariable Long parentId,
			@PathVariable String entityType,
			@RequestParam(name = "view") String view,
			@RequestParam(name = "criteria", defaultValue = "") String criteria,
			@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
			@RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
			@PageableDefault(value = 20) Pageable pageable) {
		Class<? extends BaseEntityInterface> parentEntityClass = this.entityResolverDefaultInterface.getEntityClass(parentType);
		EntityLink entityLink = new EntityLink<>(parentId, parentEntityClass);
		return getService().getCategorizedDocumentOverviews(entityLink, criteria, viewCriteria, pageable, Optional.ofNullable(sliceSelect), Optional.empty());
	}

	@GetMapping(params = { "tag" })
	@ResponseStatus(HttpStatus.OK)
	public List<DocumentDTO> getDocumentByTag(@PathVariable String parentType,
			@PathVariable Long parentId,
			@PathVariable String entityType,
			@RequestParam("tag") String tag
	) {
		Class<? extends BaseEntityInterface> parentEntityClass = this.entityResolverDefaultInterface.getEntityClass(parentType);
		return getService().getDocumentsByTag(parentEntityClass, parentId, tag);
	}

	@GetMapping(params = { "view=overview" })
	@ResponseStatus(HttpStatus.OK)
	public BaseEntityList<DocumentOverview> getDocumentsOverview(@PathVariable String parentType,
			@PathVariable Long parentId,
			@PathVariable String entityType,
			@RequestParam(name = "criteria", defaultValue = "") String criteria,
			@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
			@RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
			@PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
		Class<? extends BaseEntityInterface> parentEntityClass = this.entityResolverDefaultInterface.getEntityClass(parentType);
		EntityLink entityLink = new EntityLink<>(parentId, parentEntityClass);
		return getService().getDocumentsOverview(criteria, viewCriteria, entityLink, pageable, Optional.ofNullable(sliceSelect));
	}
}
