package com.example.mirai.services.gds.service;

import java.util.ArrayList;
import java.util.List;

import com.example.mirai.libraries.core.exception.EntityIdNotFoundException;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.services.gds.model.GdsUser;
import com.example.mirai.services.gds.repository.GdsUserRepository;
import com.example.mirai.services.gds.shared.Util;

import org.springframework.stereotype.Service;

@Service
public class GdsUserService {
	private final GdsUserRepository gdsUserRepository;

	public GdsUserService(GdsUserRepository gdsUserRepository) {
		this.gdsUserRepository = gdsUserRepository;
	}

	public User getUserByUserId(String userId) {
		GdsUser gdsUser = gdsUserRepository.getGdsUserByUserIdIs(userId);
		if (gdsUser != null)
			return Util.convertGdsUserToUser(gdsUser);
		else
			throw new EntityIdNotFoundException();
	}

	public User getUserByAbbreviation(String abbreviation) {
		GdsUser gdsUser = gdsUserRepository.getGdsUserByAbbreviationIs(abbreviation);
		if (gdsUser != null)
			return Util.convertGdsUserToUser(gdsUser);
		else
			throw new EntityIdNotFoundException();
	}

	public List<User> findUser(String searchQuery) {
		List<User> users = new ArrayList<>();
		List<GdsUser> gdsUsers = gdsUserRepository.findGdsUserByFullNameContainsOrAbbreviationIsOrUserIdIs(searchQuery, searchQuery, searchQuery);
		if (gdsUsers != null && !gdsUsers.isEmpty()) {
			gdsUsers.forEach(gdsUser ->
					users.add(Util.convertGdsUserToUser(gdsUser))
			);
		}
		return users;
	}
}
