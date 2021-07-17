package com.example.mirai.projectname.services.configuration.ruleset;

import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.example.mirai.projectname.services.configuration.ruleset.models.RuleSet;

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
@RequestMapping("/rule-sets")
public class RuleSetController {
	private final RuleSetService ruleSetService;

	public RuleSetController(RuleSetService ruleSetService) {
		this.ruleSetService = ruleSetService;
	}

	@PostMapping()
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getRuleSetAdminRoles())")
	public ResponseEntity<RuleSet> createRuleSet(@Valid @RequestBody final RuleSet ruleSet) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(ruleSetService.createRuleSet(ruleSet));
		}
		catch (EntityExistsException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<RuleSet> getRuleSet(@PathVariable String id) {
		Optional<RuleSet> ruleSet = ruleSetService.getRuleSet(id);
		if (ruleSet.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(ruleSet.get());
	}

	@GetMapping()
	public ResponseEntity<Iterable<RuleSet>> getRuleSets() {
		Iterable<RuleSet> productCategories = ruleSetService.getRuleSets();
		if (!productCategories.iterator().hasNext())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(productCategories);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getRuleSetAdminRoles())")
	public ResponseEntity<Void> deleteRuleSet(@PathVariable String id) {
		try {
			ruleSetService.deleteRuleSet(id);
			return ResponseEntity.noContent().build();
		}
		catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getRuleSetAdminRoles())")
	public ResponseEntity<RuleSet> updateRuleSet(@PathVariable String id, @Valid @RequestBody final RuleSet ruleSet) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(ruleSetService.updateRuleSet(id, ruleSet));
		}
		catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}
}
