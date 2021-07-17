package com.example.mirai.projectname.services.configuration.link;

import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.example.mirai.projectname.services.configuration.link.models.Link;

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
@RequestMapping("/links")
public class LinkController {
	private final LinkService linkService;

	public LinkController(LinkService linkService) {
		this.linkService = linkService;
	}

	@PostMapping()
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getLinkAdminRoles())")
	public ResponseEntity<Link> createLink(@Valid @RequestBody final Link link) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(linkService.createLink(link));
		}
		catch (EntityExistsException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Link> getLink(@PathVariable String id) {
		Optional<Link> link = linkService.getLink(id);
		if (link.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(link.get());
	}

	@GetMapping()
	public ResponseEntity<Iterable<Link>> getLinks() {
		Iterable<Link> productCategories = linkService.getLinks();
		if (!productCategories.iterator().hasNext())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(productCategories);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getLinkAdminRoles())")
	public ResponseEntity<Void> deleteSettings(@PathVariable String id) {
		try {
			linkService.deleteLink(id);
			return ResponseEntity.noContent().build();
		}
		catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getLinkAdminRoles())")
	public ResponseEntity<Link> updateForm(@PathVariable String id, @Valid @RequestBody final Link link) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(linkService.updateLink(id, link));
		}
		catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}
}
