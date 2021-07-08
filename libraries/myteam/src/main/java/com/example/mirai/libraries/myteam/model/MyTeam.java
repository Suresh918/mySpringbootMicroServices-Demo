package com.example.mirai.libraries.myteam.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.myteam.service.MyTeamService;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@DynamicUpdate
@Audited(withModifiedFlag = true)
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@ServiceClass(MyTeamService.class)
@Getter
@Setter
@AbacScan({ MyTeamMember.class })
public class MyTeam implements BaseEntityInterface, Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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
