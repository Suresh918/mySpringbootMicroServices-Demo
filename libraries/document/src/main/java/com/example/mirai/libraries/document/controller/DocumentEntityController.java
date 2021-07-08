package com.example.mirai.libraries.document.controller;

import com.example.mirai.libraries.core.annotation.SecurePropertyRead;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.document.model.DocumentDTO;
import com.example.mirai.libraries.document.service.DocumentService;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class DocumentEntityController extends EntityController {

	public DocumentEntityController(ObjectMapper objectMapper,
			DocumentService documentService, EntityResolverDefaultInterface entityResolver) {
		super(objectMapper, documentService, entityResolver);
	}

	DocumentService getService() {
		return ((DocumentService) (super.entityServiceDefaultInterface));
	}

	@GetMapping("/{id}/content")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ByteArrayResource> getContent(@PathVariable Long id) {
		DocumentDTO documentDTO = getService().getCompleteDocument(id);

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(documentDTO.getType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentDTO.getName() + "\"")
				.body(new ByteArrayResource(documentDTO.getContent()));
	}


	@SecurePropertyRead
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Override
	public BaseEntityInterface get(@PathVariable Long id) {
		return getService().get(id);
	}


	@PutMapping("/do-nothing")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public BaseEntityInterface update(@PathVariable Long id, @RequestBody JsonNode jsonNode) {
		return null;
	}

}
