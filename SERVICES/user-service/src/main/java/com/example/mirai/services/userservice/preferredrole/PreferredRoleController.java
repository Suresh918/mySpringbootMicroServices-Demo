package com.example.mirai.services.userservice.preferredrole;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.websecurity.PrincipalAwareJwtAuthenticationToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
@RequestMapping("/preferred-roles")
public class PreferredRoleController {
	private final PreferredRoleService preferredRoleService;

	@GetMapping()
	public ResponseEntity<PreferredRole> getPreferredRole(Principal principal) {
		User user = ((PrincipalAwareJwtAuthenticationToken) principal).getPrincipal();
		String userId = user.getUserId();

		Optional<PreferredRole> preferredRole = preferredRoleService.getPreferredRole(userId);
		if (preferredRole.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(preferredRole.get());
	}

	@PostMapping()
	public ResponseEntity<PreferredRole> updatePreferredRole(Principal principal, @RequestBody final PreferredRole preferredRole) throws JsonProcessingException {
		User user = ((PrincipalAwareJwtAuthenticationToken) principal).getPrincipal();
		String userId = user.getUserId();

		return ResponseEntity.status(HttpStatus.OK).body(preferredRoleService.updatePreferredRole(userId, preferredRole));
	}

	@GetMapping(params = "user-ids")
	public ResponseEntity<List<PreferredRole>> getPreferredRolesByUserIds(@RequestParam(name = "user-ids") String[] userIds) {
		if (preferredRoleService.getPreferredRolesByUserIds(userIds).isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.status(HttpStatus.OK).body(preferredRoleService.getPreferredRolesByUserIds(userIds));
	}

	@GetMapping(params = { "user-ids", "is-system-account=true" })
	@PreAuthorize("hasAnyRole(@mychangeUserServiceConfigurationProperties.getSdlTibcoRoles())")
	public ResponseEntity<List<PreferredRole>> getInsecurePreferredRolesByUserIds(@RequestParam(name = "user-ids") String[] userIds) {
		if (preferredRoleService.getPreferredRolesByUserIds(userIds).isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.status(HttpStatus.OK).body(preferredRoleService.getPreferredRolesByUserIds(userIds));
	}
}
