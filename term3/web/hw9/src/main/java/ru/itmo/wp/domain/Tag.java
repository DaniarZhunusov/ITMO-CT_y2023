package ru.itmo.wp.domain;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(indexes = @Index(columnList = "name", unique = true))
public class Tag {
    @Id
    @GeneratedValue
    private long id;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 32)
    @Pattern(regexp = "[a-z]+", message = "Only lowercase latin letters expected")
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}