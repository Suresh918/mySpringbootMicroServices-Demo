package com.example.mirai.services.userservice.preferredrole;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.services.userservice.core.MessagingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Data
@Slf4j
public class PreferredRoleService {
	private final PreferredRoleRepository preferredRoleRepository;

	private final JmsTemplate jmsTemplate;

	private final ObjectMapper objectMapper;

	private final PreferredRoleConfigurationProperties preferredRoleConfigurationProperties;

	private final MessagingService messagingService;

	public Optional<PreferredRole> getPreferredRole(String userId) {
		return preferredRoleRepository.findById(userId);
	}

	@Transactional
	public PreferredRole updatePreferredRole(String userId, PreferredRole preferredRole) {
		preferredRole.setUserId(userId);
		preferredRoleRepository.save(preferredRole);
		if (preferredRoleConfigurationProperties.getPublishEnabled()) {
			Event event = new Event(
					"UPDATE-PREFERRED-ROLES",
					"SUCCESS",
					preferredRole.getClass().getName(),
					preferredRole.getClass().getName(),
					null,
					preferredRole,
					null,
					System.currentTimeMillis()
			);

			messagingService.createAndSendMessage(event, preferredRoleConfigurationProperties.getDestination());
		}
		return preferredRole;
	}

	public List<PreferredRole> getPreferredRolesByUserIds(String[] userIds) {
		List<String> userIdslist = Arrays.asList(userIds);
		return (List<PreferredRole>) preferredRoleRepository.findAllById(userIdslist);
	}
}
