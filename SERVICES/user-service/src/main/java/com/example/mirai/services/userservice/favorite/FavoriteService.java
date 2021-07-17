package com.example.mirai.services.userservice.favorite;

import java.util.Objects;
import java.util.Optional;

import com.example.mirai.services.userservice.favorite.model.Favorite;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Data
@Slf4j
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;

	public Optional<Favorite> getFavorite(String userId) {
		return favoriteRepository.findById(userId);
	}

	@Transactional
	public Favorite updateFavorite(String userId, Favorite favorite) {
		Optional<Favorite> savedFavorite = favoriteRepository.findById(userId);
		log.info("Favorite available : " + savedFavorite.isPresent());
		if (savedFavorite.isPresent() && Objects.nonNull(savedFavorite.get()) && Objects.nonNull(savedFavorite.get().getCases())) {
			Favorite readInst = savedFavorite.get();
			readInst.setCases(favorite.getCases());
			log.info(" Favorite saving for" + readInst.getUserId());
			return favoriteRepository.save(readInst);
		}
		else {
			Favorite newFavorite = new Favorite(userId, favorite.getCases());
			return favoriteRepository.save(newFavorite);
		}
	}
}
