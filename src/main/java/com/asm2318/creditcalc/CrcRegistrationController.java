/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/register")
public class CrcRegistrationController {
    @GetMapping
    public String showRegistrationForm(WebRequest request, Model model) {
        CrcUser user = new CrcUser();
        model.addAttribute("newuser", user);
        return "register";
    }
    
    @PostMapping
    public ModelAndView registerUser (@ModelAttribute("newuser") @Valid CrcUserDto userDto, BindingResult result, WebRequest request, Errors errors) {

    //System.out.println("email: "+userDto.getEmail()+" // login: "+userDto.getLogin()+" // password: "+userDto.getPassword()+" // matching: "+userDto.getMatchingPassword()+" // result: "+result+" // errors: "+errors);
        CrcUser registered = new CrcUser();
        if (!result.hasErrors()) {
            registered = createUserAccount(userDto, result);
        }
        if (registered == null) {
            result.rejectValue("", "message.regError");
        }
    
        if (result.hasErrors()) {
            Map response = registerResponse(userDto, String.valueOf(errors));
            return new ModelAndView("wronglogin", response);
        }else{
            return new ModelAndView("customLogin");
        }
    
    }

    
    @Autowired
    private CrcUserService userService;
    
    
    private CrcUser createUserAccount(CrcUserDto userDto, BindingResult result) {
        CrcUser registered = null;
            try {
                registered = userService.registerNewUserAccount(userDto);
            }catch (Exception e) {
                return null;
            }    
        return registered;
    }
    
    private Map registerResponse(CrcUserDto userDto, String text){
        Map response = new HashMap();
        response.put("result", text);
        response.put("login", userDto.getLogin());
        response.put("email", userDto.getEmail());
        return response;
    }

}
