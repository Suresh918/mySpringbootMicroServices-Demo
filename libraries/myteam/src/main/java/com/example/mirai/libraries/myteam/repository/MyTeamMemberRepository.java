package com.example.mirai.libraries.myteam.repository;

import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.libraries.myteam.model.MyTeamMember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MyTeamMemberRepository extends JpaRepository<MyTeamMember, Long>, JpaSpecificationExecutor<MyTeamMember>, BaseRepository<MyTeamMember> {
	MyTeamMember findFirstByMyteam_IdAndUser_UserId(Long myTeamId, String userId);
}
