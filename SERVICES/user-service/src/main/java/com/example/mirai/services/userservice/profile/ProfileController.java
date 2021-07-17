package com.example.mirai.services.userservice.profile;

import java.security.Principal;

import com.example.mirai.libraries.websecurity.PrincipalAwareJwtAuthenticationToken;
import com.example.mirai.services.userservice.profile.model.Profile;
import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
@RequestMapping("/profiles")
public class ProfileController {
	private final ProfileService profileService;

	@GetMapping()
	public ResponseEntity<Profile> getProfile(Principal principal) {
		return ResponseEntity.status(HttpStatus.OK).body(profileService.getProfile(((PrincipalAwareJwtAuthenticationToken) principal).getToken()));
	}
}
