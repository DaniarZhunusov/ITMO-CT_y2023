package ru.itmo.wp.form;

import javax.validation.constraints.*;

@SuppressWarnings("unused")
public class NoticeForm {
    @NotBlank
    @NotNull
    @NotEmpty
    @Size(max = 255, message = "Notice content must not exceed 255 characters")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
