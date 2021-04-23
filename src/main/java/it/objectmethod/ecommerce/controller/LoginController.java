package it.objectmethod.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.objectmethod.ecommerce.dao.IArticlesDao;
import it.objectmethod.ecommerce.dao.ICartDao;
import it.objectmethod.ecommerce.dao.IUsersDao;
import it.objectmethod.ecommerce.models.Article;
import it.objectmethod.ecommerce.models.User;

@Controller
public class LoginController {

	@Autowired
	private IUsersDao userDao;
	@Autowired
	private IArticlesDao articleDao;
	@Autowired
	private ICartDao cartDao;
	
	@GetMapping("/login")
	public String prepareLogin() {
		return "LoginPage";
	}
	
	@PostMapping("/login")
	public String getUser(@RequestParam("username") String username, @RequestParam("password") String password, ModelMap map,
			HttpSession session) {
		String forwardTo = "LoginPage";
		User user = null;
		List<Article> articlesList = new ArrayList<>();
		int cartCounter = 0;
		if(username == null || password == null || username.isBlank() || password.isBlank()) {
			map.addAttribute("error", "Username and Password cannot be empty.");
		}
		else{
			user = userDao.getUser(username, password);
			if(user != null && user.getUsername().equals(username) && user.getPassword().equals(password)) {
				session.setAttribute("loggedUser", username);
				articlesList = articleDao.getArticles();
				session.setAttribute("articlesList", articlesList);
				cartCounter = cartDao.getUserArticlesNumber(username);
				session.setAttribute("user_articles", cartCounter);
				forwardTo = "ShopPage";
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
		return "LoginPage";
	}
}
