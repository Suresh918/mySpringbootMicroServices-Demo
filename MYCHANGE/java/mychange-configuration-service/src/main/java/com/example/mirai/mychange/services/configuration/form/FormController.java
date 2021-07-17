package com.example.mirai.projectname.services.configuration.form;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.example.mirai.projectname.services.configuration.form.models.FieldGroup;
import com.example.mirai.projectname.services.configuration.form.models.Form;
import com.example.mirai.projectname.services.configuration.form.models.Help;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forms")
public class FormController {
	private final FormService formService;

	public FormController(FormService formService) {
		this.formService = formService;
	}

	@PostMapping()
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getFormAdminRoles())")
	public ResponseEntity<Form> createForm(@Valid @RequestBody final Form form) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(formService.createForm(form));
		}
		catch (EntityExistsException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Form> getForm(@PathVariable String id) {
		Optional<Form> form = formService.getForm(id);
		if (form.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(form.get());
	}

	@GetMapping()
	public ResponseEntity<Iterable<Form>> getForms() {
		Iterable<Form> form = formService.getForms();
		if (!form.iterator().hasNext())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(form);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getFormAdminRoles())")
	public ResponseEntity<Void> updateForm(@PathVariable String id) {
		try {
			formService.deleteForm(id);
			return ResponseEntity.noContent().build();
		}
		catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getFormAdminRoles())")
	public ResponseEntity<Form> updateForm(@PathVariable String id, @Valid @RequestBody final Form form) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(formService.updateForm(id, form));
		}
		catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}


	@PutMapping("/{id}/fields/{fieldId}")
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getFormAdminRoles())")
	public ResponseEntity<Form> updateHelp(@PathVariable String id, @PathVariable String fieldId, @Valid @RequestBody final Help help) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(formService.updateHelp(id, fieldId, help));
		}
		catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}

	@GetMapping("/{id}/group-view")
	public ResponseEntity<List<FieldGroup>> getFieldGroupsByGroup(@PathVariable String id) {
		Optional<Form> form = formService.getForm(id);
		if (form.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

		return ResponseEntity.status(HttpStatus.OK).body(formService.getFieldGroupsByGroup(id));

	}

}
