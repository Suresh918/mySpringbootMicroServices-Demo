package com.example.mirai.libraries.document.controller;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.document.service.DocumentService;
import com.example.mirai.libraries.security.core.controller.SecurityController;

import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class DocumentSecurityController extends SecurityController {

	public DocumentSecurityController(DocumentService documentService) {
		super(documentService);
	}

	@Override
	public Class<AggregateInterface> getCaseStatusAggregateClass() {
		return null;
	}
}
