package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Tag;
import ru.itmo.wp.repository.TagRepository;

import java.util.*;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public void save(Set<Tag> tags) {
        for (Tag tag : tags) {
            Tag existingTag = tagRepository.findByName(tag.getName());
            if (existingTag == null) {
                tagRepository.save(tag);
            } else {
                tag.setId(existingTag.getId());
            }
        }
    }
}