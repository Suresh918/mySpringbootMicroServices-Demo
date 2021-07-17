package com.example.mirai.services.userservice.profile;

import java.util.Date;
import java.util.List;

import com.example.mirai.services.userservice.profile.model.Profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProfileRepository extends JpaRepository<Profile, String>, CrudRepository<Profile, String> {

	@Query("SELECT p FROM Profile p WHERE p.lastAccessedOn Between ?1 and ?2")
	List<Profile> fetchLastLoggedInUsersList(Date maxDate, Date currentDate);
}
