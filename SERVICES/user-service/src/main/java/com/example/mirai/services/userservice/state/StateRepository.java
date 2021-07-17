package com.example.mirai.services.userservice.state;

import org.springframework.data.repository.CrudRepository;

public interface StateRepository extends CrudRepository<State, String> {
	State getStateByUserId(String userId);
}
