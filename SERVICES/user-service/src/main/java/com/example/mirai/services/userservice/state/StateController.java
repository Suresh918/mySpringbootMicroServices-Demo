package com.example.mirai.services.userservice.state;

import java.security.Principal;
import java.util.Optional;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.websecurity.PrincipalAwareJwtAuthenticationToken;
import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
@RequestMapping("/states")
public class StateController {
	private final StateService stateService;

	@GetMapping()
	public ResponseEntity<State> getState(Principal principal) {
		User user = ((PrincipalAwareJwtAuthenticationToken) principal).getPrincipal();
		String userId = user.getUserId();

		Optional<State> state = stateService.getState(userId);
		if (state.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(state.get());
	}

	@PostMapping()
	public ResponseEntity<State> updateState(Principal principal, @RequestBody final State state) {
		User user = ((PrincipalAwareJwtAuthenticationToken) principal).getPrincipal();
		String userId = user.getUserId();

		return ResponseEntity.status(HttpStatus.OK).body(stateService.updateState(userId, state));
	}
}
