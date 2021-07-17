package com.example.mirai.projectname.services.configuration.tag;

import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.example.mirai.projectname.services.configuration.tag.models.Tag;
import com.example.mirai.projectname.services.configuration.util.Util;

import org.springframework.stereotype.Service;

@Service
public class TagService {
	private final TagRepository tagRepository;

	public TagService(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	public Tag createTag(Tag tag) {
		String name = Util.generateIdFromString(tag.getLabel());
		Optional<Tag> existingTag = getTag(name);
		if (existingTag.isEmpty()) {
			tag.setName(name);
			return tagRepository.save(tag);
		}
		else
			throw new EntityExistsException();
	}

	public Optional<Tag> getTag(String tagId) {
		return tagRepository.findById(tagId);
	}

	public Iterable<Tag> getTags() {
		return tagRepository.findAll();
	}

	public void deleteTag(String tagId) {
		tagRepository.deleteById(tagId);
	}

	public Tag updateTag(String tagId, Tag tag) {
		Optional<Tag> existingTag = getTag(tagId);
		if (existingTag.isEmpty())
			throw new EntityNotFoundException();
		else {
			tag.setName(tagId);
			return tagRepository.save(tag);
		}
	}

}
