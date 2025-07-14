package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.service.UserService;

@Controller
public class UserPage extends Page {
    private final UserService userService;

    public UserPage(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/user/{id}", "/user/"})
    public String user(Model model, @PathVariable(required = false) String id) {
        long userId;
        try {
            userId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            model.addAttribute("user", null);
            model.addAttribute("error", "Invalid user ID");

            return "UserPage";
        }

        User user = userService.findById(userId);
        if (user == null) {
            model.addAttribute("user", null);
        } else {
            model.addAttribute("user", user);
        }
        return "UserPage";
    }

//    @GetMapping("/user/")
//    public String user(Model model) {
//        Long userId = null;
//        User user = userService.findById(userId);
//        model.addAttribute("user", user);
//        return "UserPage";
//    }
}