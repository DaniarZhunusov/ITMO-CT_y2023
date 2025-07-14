package ru.itmo.wp.controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.exception.ValidationException;
import ru.itmo.wp.form.PostForm;
import ru.itmo.wp.form.UserCredentials;
import ru.itmo.wp.form.validator.PostFormValidator;
import ru.itmo.wp.service.JwtService;
import ru.itmo.wp.service.PostService;
import ru.itmo.wp.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {

    private final UserService userService;

    private final PostService postService;
    private final JwtService jwtService;
    private final PostFormValidator postFormValidator;

    public PostController(UserService userService, PostService postService, JwtService jwtService, PostFormValidator postFormValidator) {
        this.userService = userService;
        this.postService = postService;
        this.jwtService = jwtService;
        this.postFormValidator = postFormValidator;
    }

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postService.findAll();
    }

    @GetMapping("/post/{id}")
    public Post getPostByID(@PathVariable String id) {
        return postService.findById(Long.parseLong(id));
    }


    @InitBinder("postForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(postFormValidator);
    }

    /*@PostMapping("/posts")
    public String create(@RequestBody @Valid PostForm postForm, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new ValidationException(bindingResult);
        }
        Post post = userService.writePost(postForm);
        return jwtService.create(post);
    }*/
}
