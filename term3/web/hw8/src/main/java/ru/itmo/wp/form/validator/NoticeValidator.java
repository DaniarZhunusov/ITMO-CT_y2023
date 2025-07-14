package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.form.NoticeForm;
import ru.itmo.wp.service.NoticeService;

@Component
public class NoticeValidator implements Validator {
    private final NoticeService noticeService;

    public NoticeValidator(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    public boolean supports(Class<?> clazz) {
        return NoticeForm.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            NoticeForm noticeForm = (NoticeForm) target;
        }
    }
}