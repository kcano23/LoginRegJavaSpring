package com.LoginRegistration.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.LoginRegistration.models.User;
import com.LoginRegistration.services.UserService;
import com.LoginRegistration.validator.UserValidator;

@Controller
public class MainController {
	private final UserService userService;
	
	private final UserValidator userValidator;
    
    
    public MainController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }
	
	
    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registrationPage.jsp";
    }
    @RequestMapping("/login")
    public String login() {
        return "loginPage.jsp";
    }
    
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
    	userValidator.validate(user, result);
    	if(result.hasErrors()) {
    		return "registrationPage.jsp";
    	}
    	//TO DO after login reg works, prevent dupe emails
    	
    	//create a user with this information
    	User u = this.userService.registerUser(user);
    	session.setAttribute("userid", u.getId());
        return "redirect:/";
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // if the user is authenticated, save their user id in session
    	Boolean isLegit = this.userService.authenticateUser(email, password);
        // else, add error messages and return the login page
    	if(isLegit) {
    		User user = this.userService.findByEmail(email);
    		session.setAttribute("userid", user.getId());
    		return "redirect:/";
    	}
    	redirectAttributes.addFlashAttribute("error", "Invalid login attempt");
    	return "redirect:/login";
    }
    
    @RequestMapping("/")
    public String home(HttpSession session, Model model) {
        // get user from session, save them in the model and return the home page
    	Long id = (Long) session.getAttribute("userid");
    	User loggedinuser = this.userService.findUserById(id);
    	model.addAttribute("loggedinuser", loggedinuser);
    	return "homePage.jsp";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // invalidate session
    	session.invalidate();
        // redirect to login page
    	return "redirect:/login";
    }
//	
//	@GetMapping("/")
//	public String home() {
//		return "homePage.jsp";
//	}
}
