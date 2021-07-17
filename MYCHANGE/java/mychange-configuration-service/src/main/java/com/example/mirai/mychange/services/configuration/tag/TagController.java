package com.example.mirai.projectname.services.configuration.tag;

import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.example.mirai.projectname.services.configuration.tag.models.Tag;

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
@RequestMapping("/tags")
public class TagController {
	private final TagService tagService;

	public TagController(TagService tagService) {
		this.tagService = tagService;
	}

	@PostMapping()
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getTagAdminRoles())")
	public ResponseEntity<Tag> createTag(@Valid @RequestBody final Tag tag) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(tag));
		}
		catch (EntityExistsException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Tag> getTag(@PathVariable String id) {
		Optional<Tag> tag = tagService.getTag(id);
		if (tag.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(tag.get());
	}

	@GetMapping()
	public ResponseEntity<Iterable<Tag>> getTags() {
		Iterable<Tag> tags = tagService.getTags();
		if (!tags.iterator().hasNext())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(tags);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getTagAdminRoles())")
	public ResponseEntity<Void> deleteTag(@PathVariable String id) {
		try {
			tagService.deleteTag(id);
			return ResponseEntity.noContent().build();
		}
		catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getTagAdminRoles())")
	public ResponseEntity<Tag> updateTag(@PathVariable String id, @Valid @RequestBody final Tag tag) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(tagService.updateTag(id, tag));
		}
		catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}
}
