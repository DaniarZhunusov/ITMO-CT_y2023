package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.service.UserService;

import javax.servlet.http.HttpSession;

@Controller
public class UsersPage extends Page {
    private final UserService userService;

    public UsersPage(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/all")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "UsersPage";
    }

    @PostMapping("/users/all")
    public String setStatus(@RequestParam long userId, @RequestParam boolean newStatus, HttpSession httpSession) {
        if (getUser(httpSession) == null) {
            setMessage(httpSession, "You should enter to set status for other users");
        } else {
            User currentUser = getUser(httpSession);
            if (currentUser.getId() == userId && currentUser.isDisabled()) {
                return "redirect:/logout";
            }
            userService.updateDisabledStatus(userId, newStatus);
        }
        return "redirect:/users/all";
    }
}
