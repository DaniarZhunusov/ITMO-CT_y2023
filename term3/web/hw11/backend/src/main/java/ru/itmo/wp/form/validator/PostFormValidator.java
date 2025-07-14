package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.form.PostForm;

import java.util.HashSet;
import java.util.Set;

@Component
public class PostFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PostForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }

        PostForm postForm = (PostForm) target;
        String tagsLine = postForm.getTagsLine().trim();

        if (tagsLine.isEmpty()) {
            return;
        }

        String[] tags = tagsLine.split("\\s+");
        Set<String> tagSet = new HashSet<>();

        for (String tag : tags) {
            if (tag.isEmpty() || !tag.matches("[a-z]+")) {
                errors.rejectValue("tagsLine", "tagsLine.invalid-tags", "invalid tags");
                return;
            }
            tagSet.add(tag);
        }

        if (tags.length != tagSet.size()) {
            errors.rejectValue("tagsLine", "tagsLine.duplicate-tags", "duplicate tags are not allowed");
        }
    }
}