package com.example.mirai.services.userservice.favorite;

import java.security.Principal;
import java.util.Optional;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.websecurity.PrincipalAwareJwtAuthenticationToken;
import com.example.mirai.services.userservice.favorite.model.Favorite;
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
@RequestMapping("/favorites")
public class FavoriteController {
	private final FavoriteService favoriteService;

	@GetMapping()
	public ResponseEntity<Favorite> getFavorite(Principal principal) {
		User user = ((PrincipalAwareJwtAuthenticationToken) principal).getPrincipal();
		String userId = user.getUserId();

		Optional<Favorite> favorite = favoriteService.getFavorite(userId);
		if (favorite.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(favorite.get());
	}

	@PostMapping()
	public ResponseEntity<Favorite> updateFavorite(Principal principal, @RequestBody final Favorite favorite) {
		User user = ((PrincipalAwareJwtAuthenticationToken) principal).getPrincipal();
		String userId = user.getUserId();

		return ResponseEntity.status(HttpStatus.OK).body(favoriteService.updateFavorite(userId, favorite));
	}
}
