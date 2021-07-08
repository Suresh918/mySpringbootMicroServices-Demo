package com.example.mirai.libraries.document.service;

import java.beans.Introspector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.annotation.SecureFetchAction;
import com.example.mirai.libraries.core.annotation.SecureFetchCriteria;
import com.example.mirai.libraries.core.annotation.SecureLinkedEntityCaseAction;
import com.example.mirai.libraries.core.annotation.SecurePropertyMerge;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.EntityIdNotFoundException;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.CasePermissions;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.core.model.EntityUpdate;
import com.example.mirai.libraries.document.exception.UnableToReadContent;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.document.model.DocumentContent;
import com.example.mirai.libraries.document.model.DocumentDTO;
import com.example.mirai.libraries.document.model.dto.DocumentCategory;
import com.example.mirai.libraries.document.model.dto.DocumentOverview;
import com.example.mirai.libraries.document.repository.DocumentContentRepository;
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
import org.springframework.web.multipart.MultipartFile;

public abstract class DocumentService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface {
	private final DocumentStateMachine stateMachine;

	private final AbacProcessor abacProcessor;

	private final RbacProcessor rbacProcessor;

	private final EntityACL acl;

	private final PropertyACL pacl;

	private final CaseActionList caseActionList;

	private final DocumentContentRepository documentContentRepository;

	private DocumentService self;

	public DocumentService(DocumentStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
			EntityACL acl, PropertyACL pacl, CaseActionList caseActionList,
			DocumentContentRepository documentContentRepository) {
		this.stateMachine = stateMachine;
		this.abacProcessor = abacProcessor;
		this.rbacProcessor = rbacProcessor;
		this.acl = acl;
		this.pacl = pacl;
		this.caseActionList = caseActionList;
		this.documentContentRepository = documentContentRepository;
	}

	@PostConstruct
	private void postConstruct() {
		this.self = getSelf();
	}

	public abstract DocumentService getSelf();

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

	@SecureLinkedEntityCaseAction(caseAction = "CREATE_DOCUMENT")
	@Override
	@Transactional
	public BaseEntityInterface createLinkedEntityWithLinks(BaseEntityInterface entity,
			Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
		return EntityServiceDefaultInterface.super.createLinkedEntityWithLinks(entity, entityLinkSet);
	}

	@Transactional
	public Document createDocument(BaseEntityInterface entity, Long parentId, Class parentEntityClass,
			MultipartFile file) {
		Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
		entityLinkSet.add(new EntityLink(parentId, parentEntityClass));
		return self.create(entity, entityLinkSet, file);
	}

	@SecureLinkedEntityCaseAction(caseAction = "CREATE_DOCUMENT")
	public Document create(BaseEntityInterface entity, Set<EntityLink<BaseEntityInterface>> entityLinkSet, MultipartFile file) {
		Document document = (Document) EntityServiceDefaultInterface.super.createLinkedEntityWithLinks(entity, entityLinkSet);
		DocumentContent documentContent = new DocumentContent();
		try {
			documentContent.setContent(file.getBytes());
		}
		catch (IOException e) {
			throw new UnableToReadContent();
		}
		documentContent.setDocument(document);
		documentContentRepository.save(documentContent);
		return document;
	}

	@Transactional
	public Document createMigrationDocument(BaseEntityInterface entity, Long parentId, Class parentEntityClass,
			MultipartFile file) throws IOException {
		Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
		entityLinkSet.add(new EntityLink(parentId, parentEntityClass));
		Document document = (Document) this.createLinkedEntityWithLinks(entity, entityLinkSet);
		DocumentContent documentContent = new DocumentContent();
		documentContent.setContent(file.getBytes());
		documentContent.setDocument(document);
		documentContentRepository.save(documentContent);
		return document;
	}

	@Override
	@SecureCaseAction("READ")
	@Transactional
	public BaseEntityInterface get(Long id) {
		return EntityServiceDefaultInterface.super.get(id);
	}


	@SecureCaseAction("READ")
	@Transactional
	public DocumentDTO getCompleteDocument(Long id) {
		byte[] content = getDocumentContent(id).getContent();
		Document document = (Document) EntityServiceDefaultInterface.super.findById(id);
		DocumentDTO documentDTO = new DocumentDTO();
		documentDTO.setContent(content);
		documentDTO.setCreatedOn(document.getCreatedOn());
		documentDTO.setCreator(document.getCreator());
		documentDTO.setId(document.getId());
		documentDTO.setName(document.getName());
		documentDTO.setType(document.getType());
		documentDTO.setDescription(document.getDescription());
		documentDTO.setTags(document.getTags());
		documentDTO.setStatus(document.getStatus());
		return documentDTO;
	}

	public DocumentContent getDocumentContent(Long id) {
		return documentContentRepository.getOne(id);
	}

	@Override
	@SecureCaseAction("UPDATE")
	@SecurePropertyMerge
	@Transactional
	public Document merge(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames,
			List<String> newInsChangedAttrNames) {
		return (Document) EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttrNames);
	}

	@Override
	public CaseStatus performCaseActionAndGetCaseStatus(Long id, String action) {
		Document updatedEntity = (Document) performCaseAction(id, action);
		CaseStatus caseStatus = new CaseStatus();
		caseStatus.setStatus(updatedEntity.getStatus());
		caseStatus.setId(updatedEntity.getId());
		caseStatus.setCasePermissions(new CasePermissions(self.getCaseActions(updatedEntity.getId()), self.getCaseProperties(updatedEntity.getId())));
		return caseStatus;
	}
	//TODO check casting from BaseEntityInterface to E)

	@SecureCaseAction("REMOVE")
	@Transactional
	public Document remove(BaseEntityInterface entity) {
		EntityUpdate entityUpdate = stateMachine.remove(entity);
		DocumentService documentService = (DocumentService) ApplicationContextHolder.getService(DocumentService.class);
		return (Document) documentService.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
	}

	@SecureCaseAction("DELETE")
	@Transactional
	public void delete(Long id) {
		documentContentRepository.delete(getDocumentContent(id));
		EntityServiceDefaultInterface.super.delete(id);
	}

	@Override
	public AggregateInterface performCaseActionAndGetCaseStatusAggregate(Long aLong, String s, Class<AggregateInterface> aClass) {
		return null;
	}

	@Override
	public BaseEntityInterface performCaseAction(Long aLong, String s) {
		return null;
	}

	@Override
	public CaseStatus getCaseStatus(BaseEntityInterface baseEntityInterface) {
		return null;
	}


	@Transactional
	public DocumentDTO updateDocumentWithTag(BaseEntityInterface entity, Long parentId,
			String tag, Class parentEntityClass, MultipartFile file) {
		String parentType = parentEntityClass.getSimpleName();
		parentType = Introspector.decapitalize(parentType);
		List<Long> documentIds = getDocumentIdsByTag(parentType, parentId, tag);
		if (documentIds.size() == 1) {
			documentIds.forEach(id -> self.delete(id));
			Document document = self.createDocument(entity, parentId, parentEntityClass, file);
			return self.getCompleteDocument(document.getId());
		}
		throw new EntityIdNotFoundException();
	}

	private List<Long> getDocumentIdsByTag(String parentType, Long parentId, String tag) {
		List<String> tags = new ArrayList<>();
		tags.add(tag);
		String criteria = parentType + ".id:" + parentId + " and tags#" + tags;
		Slice<Id> idSlice = self.filterIds(criteria, PageRequest.of(0, Integer.MAX_VALUE - 1));
		return idSlice.getContent().stream().map(id -> id.getValue()).collect(Collectors.toList());
	}

	@SecureFetchAction
	public BaseEntityList<DocumentOverview> getDocumentsOverview(@SecureFetchCriteria String criteria, String viewCriteria, EntityLink entityLink, Pageable pageable, Optional<String> sliceSelect) {
		String parentEntityName = entityLink.getEClass().getSimpleName();
		parentEntityName = Introspector.decapitalize(parentEntityName);
		if (!Objects.isNull(criteria) && criteria.length() > 0)
			criteria = "(" + parentEntityName + ".id:" + entityLink.getId() + ") and (" + criteria + ")";
		else
			criteria = "(" + parentEntityName + ".id:" + entityLink.getId() + ")";

		Slice<BaseView> documentOverviewSlice = this.getEntitiesFromView(criteria, viewCriteria, pageable, sliceSelect, DocumentOverview.class);
		return new BaseEntityList(documentOverviewSlice);
	}

	public List<DocumentCategory> getCategorizedDocumentOverviews(EntityLink entityLink, String criteria,
			String viewCriteria, Pageable pageable, Optional<String> sliceSelect,
			Optional<List<DocumentCategory>> optionalOrderedCategoryList) {
		BaseEntityList<DocumentOverview> documentOverviews = getDocumentsOverview(criteria, viewCriteria, entityLink, pageable, sliceSelect);
		return groupDocumentsByCategory(documentOverviews, optionalOrderedCategoryList);
	}

	public List<DocumentDTO> getDocumentsByTag(Class parentEntityClass, Long parentId, String tag) {
		String parentType = parentEntityClass.getSimpleName();
		parentType = Introspector.decapitalize(parentType);
		List<DocumentDTO> documents = new ArrayList<>();
		List<Long> documentIds = getDocumentIdsByTag(parentType, parentId, tag);
		documentIds.forEach(id -> documents.add(self.getCompleteDocument(id)));
		return documents;
	}

	public List<DocumentCategory> groupDocumentsByCategory(BaseEntityList<DocumentOverview> documents, Optional<List<DocumentCategory>> optionalOrderedCategoryList) {
		List<DocumentCategory> orderedCategoryList = getCategoryList(documents, optionalOrderedCategoryList);

		orderedCategoryList.forEach(documentCategory -> {
			String category = documentCategory.getCategory();
			List<DocumentOverview> documentOverviewList = getDocumentsOfCategory(category, documents.getResults());
			if (Objects.nonNull(documentCategory.getDocuments()))
				documentCategory.getDocuments().addAll(documentOverviewList);
			else
				documentCategory.setDocuments(documentOverviewList);
		});
		return orderedCategoryList;
	}

	private List<DocumentCategory> getCategoryList(final BaseEntityList<DocumentOverview> documents, final Optional<List<DocumentCategory>> optionalOrderedCategoryList) {
		if (optionalOrderedCategoryList.isPresent())
			return optionalOrderedCategoryList.get();

		List<DocumentOverview> documentOverviews = documents.getResults();
		AtomicReference<String> tagCsv = new AtomicReference<>("");
		documentOverviews.forEach(documentOverview -> {
			tagCsv.set(tagCsv + documentOverview.getTags());
		});
		String[] tags = tagCsv.get().split(",");
		List<String> tagList = Arrays.stream(tags).filter(tag -> Objects.nonNull(tag) && tag.length() > 0).collect(Collectors.toList());
		List<String> tagListWithoutDuplicates = new ArrayList<>(new HashSet<>(tagList));
		List<DocumentCategory> documentCategories = new ArrayList<>();
		tagListWithoutDuplicates.stream().sorted(String::compareTo).forEachOrdered(tag -> documentCategories.add(new DocumentCategory(tag)));
		return documentCategories;
	}

	private List<DocumentOverview> getDocumentsOfCategory(final String category, final List<DocumentOverview> documents) {
		List<DocumentOverview> documentsOfCategory = new ArrayList<>();
		documents.forEach(document -> {
			String documentTagsCsv = document.getTags();
			if (documentTagsCsv.contains("," + category + ",")) {
				documentsOfCategory.add(document);
			}
		});
		return documentsOfCategory;
	}

}
