package com.example.mirai.libraries.audit.model;

import com.example.mirai.libraries.core.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditableCreator extends User {
	public AuditableCreator(User user) {
		super(user);
	}
}
