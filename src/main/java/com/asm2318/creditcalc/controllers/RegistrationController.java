package com.asm2318.creditcalc.controllers;

import com.asm2318.creditcalc.services.UserService;
import com.asm2318.creditcalc.dto.NewUserDTO;
import com.asm2318.creditcalc.entities.User;
import com.asm2318.creditcalc.utils.FieldName;
import com.asm2318.creditcalc.utils.FormName;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String showRegistrationForm(final WebRequest request, final Model model) {
        model.addAttribute("newUser", new User());
        return FormName.REGISTRATION_PAGE;
    }
    
    @PostMapping
    public ModelAndView registerUser (@ModelAttribute("newUser") @Valid final NewUserDTO userDTO) {
        try {
            userService.registerNewUser(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPassword());
            return new ModelAndView(FormName.LOGIN_PAGE);
        } catch (final Exception ex) {
            final Map<String, String> response = new HashMap<>();
            response.put(FieldName.ERROR_MESSAGE, ex.getMessage());
            response.put(FieldName.USERNAME, userDTO.getUsername());
            response.put(FieldName.EMAIL, userDTO.getEmail());
            return new ModelAndView(FormName.REGISTRATION_PAGE, response);
        }    
    }

}
