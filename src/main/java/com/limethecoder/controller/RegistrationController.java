package com.limethecoder.controller;

import com.limethecoder.data.domain.Role;
import com.limethecoder.data.domain.User;
import com.limethecoder.data.service.RoleService;
import com.limethecoder.data.service.UserService;
import com.limethecoder.util.editor.RoleEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private static final Logger logger = LoggerFactory
            .getLogger(RegistrationController.class);

    private UserService userService;
    private RoleService roleService;

    @Autowired
    private RoleEditor roleEditor;

    @Autowired
    public RegistrationController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Role.class, roleEditor);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showFormForRegistration(Model model, Authentication authentication) {

        /* Prevent from creating new account to already registered user */
        if(authentication != null &&
                !authentication.getAuthorities()
                        .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            System.out.println(authentication.getName());
            return "redirect:/";
        }

        User user = new User();
        model.addAttribute("user", user);

        if(authentication != null) {
            model.addAttribute("roles", roleService.findAll());
        }

        return "registration";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String registerUser(
            @ModelAttribute("user") @Valid User user,
            BindingResult result, HttpServletRequest request,
            Model model, Authentication authentication) {

        /* Prevent from creating new account to already registered user */
        if(authentication != null &&
                !authentication.getAuthorities()
                        .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/";
        }

        if(!result.hasErrors()) {
            user.setEnabled(true);

            String rawPass = user.getPassword();

            user = userService.add(user);
            if(user != null) {
                /* if user is not authenticated, then log in user */
                if(authentication == null) {
                    try {
                        request.login(user.getLogin(), rawPass);
                    } catch (ServletException e) {
                        logger.error("Unable to authenticate user" + e.getMessage());
                    }
                    return "redirect:/";
                }
                return "redirect:/admin/users";
            } else {
                result.rejectValue("login", "",
                        "User with such login already exists");
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());

        return "registration";
    }
}
