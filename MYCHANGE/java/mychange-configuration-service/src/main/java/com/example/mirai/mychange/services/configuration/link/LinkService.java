package com.example.mirai.projectname.services.configuration.link;

import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.example.mirai.projectname.services.configuration.link.models.Link;

import org.springframework.stereotype.Service;

@Service
public class LinkService {
	private final LinkRepository linkRepository;

	public LinkService(LinkRepository linkRepository) {
		this.linkRepository = linkRepository;
	}

	public Link createLink(Link link) {
		String name = link.getName().toLowerCase();
		Optional<Link> existingLink = getLink(name);
		if (existingLink.isEmpty()) {
			link.setName(name);
			return linkRepository.save(link);
		}
		else
			throw new EntityExistsException();
	}

	public Optional<Link> getLink(String linkId) {
		return linkRepository.findById(linkId);
	}

	public Iterable<Link> getLinks() {
		return linkRepository.findAll();
	}

	public void deleteLink(String linkId) {
		linkRepository.deleteById(linkId);
	}

	public Link updateLink(String linkId, Link link) {
		Optional<Link> existingLink = getLink(linkId);
		if (existingLink.isEmpty())
			throw new EntityNotFoundException();
		else {
			link.setName(linkId);
			return linkRepository.save(link);
		}
	}
}
