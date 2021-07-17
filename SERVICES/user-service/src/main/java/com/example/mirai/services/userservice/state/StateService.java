package com.example.mirai.services.userservice.state;

import java.util.Optional;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
public class StateService {
	private final StateRepository stateRepository;

	public StateService(StateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	public Optional<State> getState(String userId) {
		return stateRepository.findById(userId);
	}

	public State updateState(String userId, State state) {
		boolean stateAvailable = stateRepository.existsById(userId);
		if (stateAvailable) {
			log.info("state available for : " + userId);
			State existingState = stateRepository.getStateByUserId(userId);
			existingState.setState(state.getState());
			log.info("updating state for : " + existingState.getUserId());
			return stateRepository.save(existingState);
		}
		else {
			log.info("state not available for : " + userId);
			State newState = new State(userId, state.getState());
			log.info("creating state for : " + newState.getUserId());
			return stateRepository.save(newState);
		}
	}
}
