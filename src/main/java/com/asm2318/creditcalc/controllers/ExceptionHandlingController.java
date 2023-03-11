package com.asm2318.creditcalc.controllers;

import com.asm2318.creditcalc.utils.FieldName;
import com.asm2318.creditcalc.utils.FormName;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionHandlingController {
    
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(final HttpServletRequest request, final Exception ex) {

    final ModelAndView result = new ModelAndView()
            .addObject(FieldName.ERROR_MESSAGE, ex.toString());
    result.setViewName(FormName.ERROR_PAGE);
    return result;
  }
}
