package it.objectmethod.ecommerce.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.objectmethod.ecommerce.dao.IUsersDao;
import it.objectmethod.ecommerce.models.User;

@Controller
public class LoginController {

	@Autowired
	private IUsersDao userDao;
	
	@GetMapping("/login")
	public String loadLoginPage() {
		return "LoginPage";
	}
	
	@PostMapping("/login")
	public String getUser(@RequestParam("username") String username, @RequestParam("password") String password, ModelMap map,
			HttpSession session) {
		String forwardTo = "LoginPage";
		User user = null;
		if(username == null || password == null || username.isBlank() || password.isBlank()) {
			map.addAttribute("error", "Username and Password cannot be empty.");
		}
		else{
			user = userDao.getUser(username, password);
			if(user != null && user.getUsername().equals(username) && user.getPassword().equals(password)) {
				session.setAttribute("loggedUser", username);
				forwardTo = "redirect:/articles";
			}
			else {
				map.addAttribute("error", "Wrong Username or Password.");
			}
		}
		return forwardTo;
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
}
