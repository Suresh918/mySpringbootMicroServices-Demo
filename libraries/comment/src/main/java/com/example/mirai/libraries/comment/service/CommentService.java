package com.example.mirai.libraries.comment.service;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.example.mirai.libraries.comment.model.Comment;
import com.example.mirai.libraries.comment.model.CommentCaseActions;
import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.comment.model.dto.CommentOverview;
import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.annotation.SecureFetchAction;
import com.example.mirai.libraries.core.annotation.SecureFetchCriteria;
import com.example.mirai.libraries.core.annotation.SecureLinkedEntityCaseAction;
import com.example.mirai.libraries.core.annotation.SecurePropertyMerge;
import com.example.mirai.libraries.core.exception.CaseActionNotFoundException;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.CasePermissions;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.core.model.EntityUpdate;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.document.service.DocumentService;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.security.abac.AbacAwareInterface;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.security.rbac.RbacAwareInterface;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

public abstract class CommentService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface {
	private final CommentStateMachine stateMachine;

	private final AbacProcessor abacProcessor;

	private final RbacProcessor rbacProcessor;

	private final EntityACL acl;

	private final PropertyACL pacl;

	private final CaseActionList caseActionList;

	protected DocumentService documentService;

	@Resource
	private CommentService self;

	public CommentService(DocumentService documentService, CommentStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
			EntityACL acl, PropertyACL pacl, CaseActionList caseActionList) {
		this.stateMachine = stateMachine;
		this.abacProcessor = abacProcessor;
		this.rbacProcessor = rbacProcessor;
		this.acl = acl;
		this.pacl = pacl;
		this.documentService = documentService;
		this.caseActionList = caseActionList;
	}

	@Override
	public EntityACL getEntityACL() {
		return acl;
	}

	@Override
	public PropertyACL getPropertyACL() {
		return pacl;
	}

	@Override
	public CaseActionList getCaseActionList() {
		return caseActionList;
	}

	@Override
	public AbacAwareInterface getABACAware() {
		return abacProcessor;
	}

	@Override
	public RbacAwareInterface getRBACAware() {
		return rbacProcessor;
	}

	@SecureLinkedEntityCaseAction(caseAction = "CREATE_COMMENT")
	@Override
	@Transactional
	public BaseEntityInterface createLinkedEntityWithLinks(BaseEntityInterface entity, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
		return EntityServiceDefaultInterface.super.createLinkedEntityWithLinks(entity, entityLinkSet);
	}

	@Override
	@SecureCaseAction("UPDATE")
	@SecurePropertyMerge
	@Transactional
	public Comment merge(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames,
			List<String> newInsChangedAttrNames) {
		return (Comment) EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttrNames);
	}

	@Override
	public CaseStatus performCaseActionAndGetCaseStatus(Long id, String action) {
		Comment updatedEntity = (Comment) performCaseAction(id, action);
		return getCaseStatus(updatedEntity);
	}

	public CaseStatus performCaseActionAndGetCaseStatus(String action, BaseEntityInterface comment) {
		Comment updatedEntity = (Comment) performCaseAction(action, comment);
		return getCaseStatus(updatedEntity);
	}

	public BaseEntityInterface performCaseAction(Long id, String action) {
		BaseEntityInterface entity = self.getEntityById(id);
		switch (CommentCaseActions.valueOf(action.toUpperCase())) {
			case REMOVE:
				return self.remove(entity);
			case PUBLISH:
				return self.publish(entity);
			default:
				throw new CaseActionNotFoundException();
		}
	}

	public BaseEntityInterface performCaseAction(String action, BaseEntityInterface comment) {
		Comment entity = (Comment) self.getEntityById(comment.getId());
		entity.setCommentText(((Comment) comment).getCommentText());
		switch (CommentCaseActions.valueOf(action.toUpperCase())) {
			case REMOVE:
				return self.remove(entity);
			case PUBLISH:
				return self.publish(entity);
			default:
				throw new CaseActionNotFoundException();
		}
	}

	public CaseStatus getCaseStatus(Comment updatedEntity) {
		CaseStatus caseStatus = new CaseStatus();
		caseStatus.setId(updatedEntity.getId());
		caseStatus.setStatus(updatedEntity.getStatus());
		caseStatus.setStatusLabel(CommentStatus.getLabelByCode(updatedEntity.getStatus()));
		caseStatus.setCasePermissions(new CasePermissions(self.getCaseActions(updatedEntity), self.getCaseProperties(updatedEntity)));
		return caseStatus;
	}

	@SecureCaseAction("REMOVE")
	@Transactional
	public Comment remove(BaseEntityInterface entity) {
		EntityUpdate entityUpdate = stateMachine.remove(entity);
		return (Comment) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
	}

	@SecureCaseAction("REMOVE")
	@Transactional
	public Comment remove(BaseEntityInterface entity, List<Document> documents) {
		documents.forEach(document -> documentService.delete(document.getId()));
		EntityUpdate entityUpdate = stateMachine.remove(entity);
		return (Comment) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
	}


	@SecureCaseAction("PUBLISH")
	@Transactional
	public Comment publish(BaseEntityInterface entity) {
		EntityUpdate entityUpdate = stateMachine.publish(entity);
		return (Comment) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
	}

	@SecureCaseAction("DELETE")
	@Transactional
	public void delete(Long id, List<Document> documents) {
		Comment comment = (Comment) this.getById(id);
		if (comment.getStatus() == 1) {
			documents.forEach(document -> documentService.delete(document.getId()));
			EntityServiceDefaultInterface.super.delete(id);
		}
		else {
			throw new InternalAssertionException("Deletion not allowed");
		}
	}

	@Override
	public AggregateInterface performCaseActionAndGetCaseStatusAggregate(Long aLong, String s, Class<AggregateInterface> aClass) {
		return null;
	}

	@Override
	public CaseStatus getCaseStatus(BaseEntityInterface baseEntityInterface) {
		return null;
	}

	public BaseEntityList<CommentOverview> getCommentsOverviewByParent(String criteria, Class<? extends BaseEntityInterface> parentEntityClass, EntityLink entityLink, Pageable pageable, Optional<String> sliceSelect) {
		return self.getCommentsOverview(criteria, parentEntityClass, entityLink, pageable, sliceSelect);
	}

	@SecureFetchAction
	public BaseEntityList<CommentOverview> getCommentsOverview(@SecureFetchCriteria String criteria, Class<? extends BaseEntityInterface> parentEntityClass, EntityLink entityLink, Pageable pageable, Optional<String> sliceSelect) {
		String parentEntityName = parentEntityClass.getSimpleName();
		parentEntityName = Introspector.decapitalize(parentEntityName);
		parentEntityName = parentEntityName.toLowerCase().endsWith("comment") ? "replyTo" : parentEntityName;
		if (!Objects.isNull(criteria) && criteria.length() > 0)
			criteria = "(" + parentEntityName + ".id:" + entityLink.getId() + ") and (" + criteria + ")";
		else
			criteria = "(" + parentEntityName + ".id:" + entityLink.getId() + ")";
		Slice<BaseView> commentOverviewSlice = this.getEntitiesFromViewFilterOnEntity(criteria, pageable, sliceSelect, CommentOverview.class);
		List<BaseView> commentOverviewList = commentOverviewSlice.getContent();
		commentOverviewList.stream().forEach(baseView -> {
			Long commentId = ((CommentOverview) baseView).getId();
			((CommentOverview) baseView).setCasePermissions(new CasePermissions(getCaseActions(commentId), getCaseProperties(commentId)));
			//((CommentOverview) baseView).setDocuments((documentService).findDocumentsByCommentIdAndStatus(commentId, DocumentStatus.PUBLISHED.getStatusCode()));
		});
		return new BaseEntityList(commentOverviewSlice);
	}

	public List<Long> getCommentIdsByParent(EntityLink entityLink) {
		String parentEntityName = entityLink.getEClass().getSimpleName();
		parentEntityName = Introspector.decapitalize(parentEntityName);
		String criteria = "(" + parentEntityName + ".id:" + entityLink.getId() + ")";
		Slice<Id> commentIdSlice = filterIds(criteria, PageRequest.of(0, Integer.MAX_VALUE - 1));
		List<Long> commentIds = commentIdSlice.getContent().stream().map(item -> item.getValue()).collect(Collectors.toList());
		List<Long> replyIds = new ArrayList<>();
		commentIds.forEach(commentId -> {
			Slice<Id> replyIdSlice = filterIds("replyTo.id:" + commentId, PageRequest.of(0, Integer.MAX_VALUE - 1));
			if (replyIdSlice.getNumberOfElements() > 0) {
				replyIds.addAll(replyIdSlice.getContent().stream().map(item -> item.getValue()).collect(Collectors.toList()));
			}
		});
		commentIds.addAll(replyIds);
		return commentIds;
	}
}
