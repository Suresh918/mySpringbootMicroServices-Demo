package com.example.mirai.services.userservice.profile.model;

import java.io.Serializable;
import java.util.List;

import com.example.mirai.libraries.core.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastLoggedInReport implements Serializable {
	List<User> userIdsList;

	List<User> recipientUsersList;
}
