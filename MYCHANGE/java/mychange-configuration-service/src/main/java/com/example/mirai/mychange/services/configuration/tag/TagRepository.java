package com.example.mirai.projectname.services.configuration.tag;

import com.example.mirai.projectname.services.configuration.tag.models.Tag;

import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<Tag, String> {
}
