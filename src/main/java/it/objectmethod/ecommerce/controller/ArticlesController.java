package it.objectmethod.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import it.objectmethod.ecommerce.dao.IArticlesDao;
import it.objectmethod.ecommerce.models.Article;

@Controller
public class ArticlesController {

	@Autowired
	private IArticlesDao articleDao;
	
	@GetMapping("/articles")
	public String getArticlesList(HttpSession session) {
		List<Article> articlesList = new ArrayList<>();
		articlesList = articleDao.getArticles();
		session.setAttribute("articlesList", articlesList);
		return "redirect:/shop";
	}
}
