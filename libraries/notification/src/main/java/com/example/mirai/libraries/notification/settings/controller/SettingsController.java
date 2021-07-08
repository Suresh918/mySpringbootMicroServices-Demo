package com.example.mirai.libraries.notification.settings.controller;

import java.security.Principal;
import java.util.Optional;

import javax.validation.Valid;

import com.example.mirai.libraries.core.exception.UnauthorizedException;
import com.example.mirai.libraries.notification.settings.model.Settings;
import com.example.mirai.libraries.notification.settings.service.SettingsService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettingsController {

	private final SettingsService settingsService;

	public SettingsController(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	@PostMapping("/settings")
	public ResponseEntity<Settings> createSettings(@RequestBody final Settings settings) {
		return ResponseEntity.status(HttpStatus.CREATED).body(settingsService.createSettings(settings));
	}

	@GetMapping("/settings/{id}")
	public ResponseEntity<Settings> getSettings(@PathVariable String id) {
		Optional<Settings> settings = settingsService.getSettings(id);
		if (settings.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.ok(settings.get());
	}

	@PutMapping("/settings/{id}")
	public ResponseEntity<Settings> updateSettings(Principal principal,@RequestBody final Settings settings, @PathVariable String id) {
		settings.setUserId(id);
		return ResponseEntity.ok(settingsService.updateSettings(principal,settings));
	}

	@DeleteMapping("/settings/{id}")
	public ResponseEntity<Void> deleteSettings(@PathVariable String id) {
		try {
			settingsService.deleteSettings(id);
			return ResponseEntity.noContent().build();
		}
		catch (UnauthorizedException e) {
			throw new UnauthorizedException();
		}
		catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

}
