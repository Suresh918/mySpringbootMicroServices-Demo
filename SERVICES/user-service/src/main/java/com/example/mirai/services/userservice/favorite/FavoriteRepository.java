package com.example.mirai.services.userservice.favorite;

import com.example.mirai.services.userservice.favorite.model.Favorite;

import org.springframework.data.repository.CrudRepository;

public interface FavoriteRepository extends CrudRepository<Favorite, String> {
}
