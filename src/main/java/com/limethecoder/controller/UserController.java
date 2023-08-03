package com.limethecoder.controller;

import com.limethecoder.data.domain.Role;
import com.limethecoder.data.domain.User;
import com.limethecoder.data.service.RoleService;
import com.limethecoder.data.service.UserService;
import com.limethecoder.util.editor.RoleEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    private static final Logger logger = LoggerFactory
            .getLogger(UserController.class);

    private UserService userService;
    private RoleService roleService;

    @Autowired
    private RoleEditor roleEditor;

    private final static int PAGE_SIZE = 30;
    private final static int PAGES_ON_VIEW = 5;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Role.class, roleEditor);
    }

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @RequestMapping(method=RequestMethod.GET)
    public String usersList(@RequestParam(name = "page", defaultValue = "1")
                                        int pageNumber, Model model) {
        if(pageNumber > 0) {
            PageRequest pageRequest = new PageRequest(pageNumber - 1, PAGE_SIZE);
            Page<User> page = userService.findAll(pageRequest);

            if(page.getTotalElements() == 0) {
                model.addAttribute("error", "No users in database");
                return "users";
            }

            int begin = Math.max(1, pageNumber - PAGES_ON_VIEW / 2);
            int end = Math.min(begin + PAGES_ON_VIEW - 1, page.getTotalPages());

            if(pageNumber > end) {
                model.addAttribute("message", "Page number out of range");
                return "error";
            }

            if(end - pageNumber < PAGES_ON_VIEW / 2) {
                begin = Math.max(1, end - PAGES_ON_VIEW + 1);
            }

            model.addAttribute("current", pageNumber);
            model.addAttribute("begin", begin);
            model.addAttribute("end", end);
            model.addAttribute("users", page);

        } else {
            model.addAttribute("message", "Page number can't be less that 1");
            return "error";
        }

        return "users";
    }

    @RequestMapping(value = "/{login}", method = RequestMethod.GET)
    public String userDetail(@PathVariable String login, Model model) {
        User user = userService.findOne(login);
        if(user == null) {
            model.addAttribute("message", "No user with login " + login);
            return "error";
        }

        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());

        return "user_details";
    }

    @RequestMapping(value = "/{login}", method = RequestMethod.POST, params = "edit_btn")
    public String editUser(Model model,
                           @ModelAttribute("user") @Valid User user,
                           BindingResult result) {

        if (result.hasErrors()) {
            logger.info(result.toString());
            model.addAttribute("user", user);
            model.addAttribute("roles", roleService.findAll());

            return "user_details";
        }

        userService.update(user);

        return "redirect:/admin/users";
    }


    @RequestMapping(value = "/{login}", method = RequestMethod.POST, params = "delete_btn")
    public String deleteUser(@PathVariable String login, Model model) {
        if (userService.findOne(login) == null) {
            model.addAttribute("message", "No user with login " + login);
            return "error";
        }
        userService.delete(login);
        return "redirect:/admin/users";
    }
}
