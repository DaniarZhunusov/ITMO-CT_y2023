package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.Tag;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.PostForm;
import ru.itmo.wp.repository.PostRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagService tagService;

    public PostService(PostRepository postRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
    }

    public List<Post> findAll() {
        return postRepository.findAllByOrderByCreationTimeDesc();
    }

    public Post findById(Long id) {
        if (id == null) {
            return null;
        }
        return postRepository.findById(id).orElse(null);
    }

    public void writeComment(Post post, User user, Comment comment) {
        post.addComment(comment);
        comment.setPost(post);
        comment.setUser(user);
        postRepository.save(post);
    }

    public Post getPostFrom(PostForm postForm) {
        Post post = new Post();
        post.setTitle(postForm.getTitle());
        post.setText(postForm.getText());

        String tagsLine = postForm.getTagsLine().trim();
        if (!tagsLine.isEmpty()) {
            Set<Tag> tags = Arrays.stream(tagsLine.split("\\s+"))
                    .map(tagName -> {
                        Tag tag = new Tag();
                        tag.setName(tagName);
                        return tag;
                    })
                    .collect(Collectors.toSet());

            tagService.save(tags);
            post.setTags(tags);
        }
        return post;
    }
}