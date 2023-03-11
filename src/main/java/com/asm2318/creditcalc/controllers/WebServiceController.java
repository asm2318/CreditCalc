package com.asm2318.creditcalc.controllers;

import com.asm2318.creditcalc.dto.CalculationRequestDTO;
import com.asm2318.creditcalc.dto.AdministratorRequestDTO;
import com.asm2318.creditcalc.entities.User;
import com.asm2318.creditcalc.enums.Authority;
import com.asm2318.creditcalc.services.AdministratorService;
import com.asm2318.creditcalc.utils.JsonConverter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import com.asm2318.creditcalc.services.UserService;
import com.asm2318.creditcalc.utils.AuthorityHelper;
import com.asm2318.creditcalc.utils.FieldName;
import com.asm2318.creditcalc.utils.FormName;
import com.asm2318.creditcalc.validators.LoanParameter;
import java.io.UnsupportedEncodingException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Controller
public class WebServiceController {
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private AdministratorService administratorService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String redirectToLoginForm(){ return "/loginForm";}
    
    @GetMapping("/calculationNew")
    public ModelAndView openNewCalculationForm(final CalculationRequestDTO dto){
        final Map<String, Object> params = new HashMap<>();
        params.put(FieldName.CALCULATOR_SUM, dto.getSum());
        params.put(FieldName.CALCULATOR_RATE, dto.getRate());
        params.put(FieldName.CALCULATOR_TERM, dto.getTerm());
        params.put(FieldName.CALCULATOR_DATE, dto.getFirstPayDate());
        params.put(FieldName.CALCULATOR_ANNUITY, Boolean.toString(dto.isAnnuity()));
        return new ModelAndView(FormName.CALCULATION_DATA_PAGE, params);
    }  
     
    @GetMapping("/requestsHistory")
    public ModelAndView showRequestsHistory(final AdministratorRequestDTO dto){
        final String dateStart = dto.getDateStart();
        final String dateEnd = dto.getDateEnd();
        final Map<String, String> response = new HashMap<>();
        response.put(FieldName.REQUESTS_HISTORY, 
                JsonConverter.convertRequestsHistory(administratorService.getRequestsHistory(dateStart, dateEnd))
        );
        response.put(FieldName.DATE_START, dateStart);
        response.put(FieldName.DATE_END, dateEnd);
        return new ModelAndView(FormName.ADMIN_PAGE, response);
    }
    
    private ModelAndView getUsersList() {
        final Map response = new HashMap();
        response.put(FieldName.USERS_LIST, JsonConverter.convertUsersList(administratorService.getUsersList()));
        response.put(FieldName.ROLES_LIST, JsonConverter.convertRolesList(administratorService.getRolesList()));
        return new ModelAndView(FormName.ADMIN_PAGE, response);
    }
    
    @GetMapping("/usersList")
    public ModelAndView showUsers(){
        return getUsersList();
    }
    
    @PostMapping("/roleUpdate")
    public ModelAndView updateUserRole(final AdministratorRequestDTO dto){
        administratorService.updateUserRole(dto.getUserId(), dto.getUpdateRoleId());
        return getUsersList();
    }
    
    @GetMapping("/userPage")
    public ModelAndView getUserPage (final Authentication authentication){
        if (AuthorityHelper.userHasAuthority(authentication.getPrincipal(), Authority.ADMIN))
            return new ModelAndView(FormName.ADMIN_PAGE);
        final Map<String, Object> params = new HashMap<>();
        params.put(FieldName.CALCULATOR_SUM, LoanParameter.SUM_MIN);
        params.put(FieldName.CALCULATOR_RATE, LoanParameter.RATE_MIN);
        params.put(FieldName.CALCULATOR_TERM, LoanParameter.TERM_MIN);
        return new ModelAndView(FormName.CALCULATION_DATA_PAGE, params);
    }
        
    @PostMapping("/calculation")
    public ModelAndView calculatePayments (final Authentication authentication, final CalculationRequestDTO dto) 
            throws UnsupportedEncodingException{
        final Map<String, Object> response = userService.calculatePayments(
                ((User)authentication.getPrincipal()).getUserId(),
                ((WebAuthenticationDetails)authentication.getDetails()).getRemoteAddress(),
                httpServletRequest != null ? httpServletRequest.getRequestURI() : "Undefined",
                dto.getFirstPayDate(),
                dto.getSum(),
                dto.getRate(),
                dto.isAnnuity(),
                dto.getTerm()
        );
        return new ModelAndView(response.get(FieldName.MVC_FIELD).toString(), response);
    }
    
}
