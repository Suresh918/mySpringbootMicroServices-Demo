package com.example.mirai.services.userservice.profile;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.websecurity.conf.RoleConfiguration;
import com.example.mirai.services.userservice.core.MessagingService;
import com.example.mirai.services.userservice.profile.model.LastLoggedInReport;
import com.example.mirai.services.userservice.profile.model.Profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Data
@Slf4j
public class ProfileService {

	private final RoleConfiguration roleConfiguration;

	private final ProfileConfigurationProperties profileConfigurationProperties;

	private final ProfileRepository profileRepository;

	private final ObjectMapper objectMapper;

	private final JmsTemplate jmsTemplate;

	private final MessagingService messagingService;

	private Profile convertJwtToProfile(Jwt jwt) {
		List<String> roles = Arrays.asList(profileConfigurationProperties.getRoles());
		Profile profile = new Profile();
		profile.setUserId(getClaimAsString(jwt, "user_id"));
		profile.setEmail(getClaimAsString(jwt, "email"));
		profile.setEmployeeNumber(getClaimAsString(jwt, "employee_number"));
		profile.setFullName(getClaimAsString(jwt, "full_name"));
		profile.setAbbreviation(getClaimAsString(jwt, "abbreviation"));
		profile.setDepartmentNumber(getClaimAsString(jwt, "department_number"));
		profile.setDepartmentName(getClaimAsString(jwt, "department_name"));
		List<String> groupMemberships = jwt.getClaimAsStringList("group_membership");
		List<String> rolesFromGroupMemberships = getRolesFromGroupMemberships(groupMemberships);
		String[] rolesList = rolesFromGroupMemberships.stream()
				.filter(rolesFromGroupMembership -> roles.contains(rolesFromGroupMembership)).toArray(String[]::new);

		List<String> rolesListWithoutDuplicates = Arrays.asList(rolesList).stream()
				.distinct().sorted()
				.collect(Collectors.toList());

		List<String> memberShipList = getMemberShips(groupMemberships);

		List<String> memberShipListWithoutDuplicates = memberShipList.stream()
				.distinct().sorted()
				.collect(Collectors.toList());

		profile.setRoles(rolesListWithoutDuplicates.toArray(String[]::new));

		profile.setMemberships(memberShipListWithoutDuplicates.toArray(String[]::new));

		return profile;
	}

	public List getRolesFromGroupMemberships(List<String> groupMemberships) {
		if (groupMemberships == null)
			return new ArrayList();
		return groupMemberships.stream().map(groupMembership -> roleConfiguration.getRole(groupMembership.toLowerCase())).collect(Collectors.toList());
	}

	public List<String> getMemberShips(List<String> groupMemberships) {
		if (Objects.isNull(groupMemberships))
			return new ArrayList<>();
		List<String> memberShipList = new ArrayList<>();
		groupMemberships.stream().forEach(membership -> {
			if (Objects.isNull(roleConfiguration.getRole(membership.toLowerCase())))
				memberShipList.add(membership);
		});
		return memberShipList;
	}


	private String getClaimAsString(Jwt jwt, String claim) {
		if (jwt == null)
			return null;
		List<String> claims = jwt.getClaimAsStringList(claim);
		if (claims == null)
			return null;
		return claims.get(0);
	}

	private Date getNow() {
		return new Timestamp(System.currentTimeMillis());
	}

	@Transactional
	public Profile createOrUpdateProfile(Jwt jwt) {
		String userId = getClaimAsString(jwt, "user_id");
		Optional<Profile> optionalProfile = profileRepository.findById(userId);
		Profile profile = convertJwtToProfile(jwt);
		if (optionalProfile.isEmpty()) {
			User actor = new User();
			actor.setUserId(profile.getUserId());
			actor.setFullName(profile.getFullName());
			actor.setEmail(profile.getEmail());
			actor.setDepartmentName(profile.getDepartmentName());
			actor.setAbbreviation(profile.getAbbreviation());
			if (profileConfigurationProperties.getPublishEnabled()) {
				Event event = new Event("PROFILE_CREATED",
						"SUCCESS",
						profile.getClass().getName(),
						profile.getClass().getName(),
						actor,
						profile.getClass().getName(),
						null,
						System.currentTimeMillis());

				messagingService.createAndSendMessage(event, profileConfigurationProperties.getDestination());
			}
		}
		profile.setLastAccessedOn(getNow());

		profileRepository.save(profile);
		return profile;
	}

	public Profile getProfile(Jwt jwt) {
		return createOrUpdateProfile(jwt);
	}

	@Scheduled(cron = "${mirai.services.user-service.profile.last-logged-in-report.cron}")
	@SchedulerLock(name = "publishLastLoggedInReport")
	public void publishLastLoggedInReport() {
		if (!profileConfigurationProperties.getLastLoggedInReport().getPublishEnabled()) {
			return;
		}
		Timestamp currentDate = new Timestamp(new Date().getTime());
		Instant before = Instant.now().minus(Duration.ofDays(profileConfigurationProperties.getLastLoggedInReport().getPastDays()));
		Timestamp dateBefore = new Timestamp(Date.from(before).getTime());
		List<Profile> profileList = profileRepository.fetchLastLoggedInUsersList(dateBefore, currentDate);
		List<User> lastLoggedInUsersList = new ArrayList<>();
		profileList.stream().forEach(profile -> {
			User user = new User();
			user.setUserId(profile.getUserId());
			user.setEmail(profile.getEmail());
			user.setFullName(profile.getFullName());
			user.setDepartmentName(profile.getDepartmentName());
			user.setAbbreviation(profile.getAbbreviation());
			lastLoggedInUsersList.add(user);
		});

		LastLoggedInReport lastLoggedInReport = new LastLoggedInReport();
		lastLoggedInReport.setUserIdsList(lastLoggedInUsersList);
		List<User> recipientUsers = new ArrayList<>();

		profileConfigurationProperties.getLastLoggedInReport().getRecipients()
				.forEach(userEmail -> {
					User recipientUser = new User();
					recipientUser.setEmail(userEmail);
					recipientUsers.add(recipientUser);
				});
		lastLoggedInReport.setRecipientUsersList(recipientUsers);

		Event event = new Event(
				"LAST_LOGGED_IN_REPORT",
				"SUCCESS",
				lastLoggedInReport.getClass().getName(),
				lastLoggedInReport.getClass().getName(),
				null,
				lastLoggedInReport,
				null,
				System.currentTimeMillis()
		);

		messagingService.createAndSendMessage(event, profileConfigurationProperties.getLastLoggedInReport().getDestination());
	}
}
