package com.example.mirai.libraries.myteam.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;

import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.myteam.service.MyTeamMemberService;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.libraries.security.abac.annotation.AbacSubject;
import com.example.mirai.libraries.security.abac.annotation.AbacSubjects;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Audited(withModifiedFlag = true)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@ServiceClass(MyTeamMemberService.class)
@Getter
@Setter
public class MyTeamMember implements BaseEntityInterface, Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@AbacSubjects(value = {
			@AbacSubject(role = "ValueOfProperty:roles", principal = "userId"),
			@AbacSubject(role = "Static:user", principal = "userId")
	})
	private User user;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "member_role", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
	@OrderColumn
	private List<String> roles;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "myteam_id")
	@JsonIgnore
	@AbacScan
	private MyTeam myteam;

	@Override
	public Integer getStatus() {
		return null;
	}

	@Override
	public void setStatus(Integer integer) {
	}

	@Override
	public List<ContextInterface> getContextsAsContextInterface() {
		return null;
	}
}
