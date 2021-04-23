package it.objectmethod.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.objectmethod.ecommerce.dao.ICartDao;
import it.objectmethod.ecommerce.models.CartArticle;

@Controller
public class CartController {

	@Autowired
	private ICartDao cartDao;
	
	@GetMapping("/shop")
	public String loadShopPage(HttpSession session) {
		String username = (String) session.getAttribute("loggedUser");
		String forwardTo = "LoginPage";
		int cartCounter = 0;
		if(username != null) {
			forwardTo = "ShopPage";
			cartCounter = cartDao.getUserArticlesNumber(username);
			session.setAttribute("user_articles", cartCounter);
		}
		return forwardTo;
	}

	@GetMapping("/addToCart/article/{articleId}")
	public String addToCart(@PathVariable("articleId") int id, HttpSession session) {
		String username = (String) session.getAttribute("loggedUser");
		int cartCounter = 0;
		int previousQuantity = 0;
		if(cartDao.isArticleAlreadyInTheCart(username, id)) {
			//UPDATE
			previousQuantity = cartDao.getPreviousQuantity(username, id);
			cartDao.updateArticleQuantity(username, id, previousQuantity + 1);
		}
		else {
			//INSERT INTO
			cartDao.addToCart(username, id, 1);
		}
		cartCounter = cartDao.getUserArticlesNumber(username);
		session.setAttribute("user_articles", cartCounter);
		return "ShopPage";
	}
	
	@GetMapping("/cart")
	public String getTotal(HttpSession session) {
		String username = (String) session.getAttribute("loggedUser");
		List<CartArticle> cartList = new ArrayList<>();
		double total = 0;
		cartList = cartDao.getUserCartList(username);
		total = cartDao.getTotal(username);
		session.setAttribute("cartList", cartList);
		session.setAttribute("total", total);
		return "CartPage";
	}
	
	@GetMapping("/addOne/article/{articleId}")
	public String addOneArticle(@PathVariable("articleId") int id, HttpSession session) {
		String username = (String) session.getAttribute("loggedUser");
		List<CartArticle> cartList = new ArrayList<>();
		int cartCounter = 0;
		double total = 0;
		int previousQuantity = 0;
		previousQuantity = cartDao.getPreviousQuantity(username, id);
		cartDao.updateArticleQuantity(username, id, previousQuantity + 1);
		cartList = cartDao.getUserCartList(username);
		cartCounter = cartDao.getUserArticlesNumber(username);
		total = cartDao.getTotal(username);
		session.setAttribute("total", total);
		session.setAttribute("user_articles", cartCounter);
		session.setAttribute("cartList", cartList);
		return "CartPage";
	}
	
	@GetMapping("/removeOne/article/{articleId}")
	public String removeOneArticle(@PathVariable("articleId") int id, HttpSession session) {
		String username = (String) session.getAttribute("loggedUser");
		List<CartArticle> cartList = new ArrayList<>();
		int cartCounter = 0;
		double total = 0;
		int previousQuantity = 0;
		int newQuantity = 0;
		previousQuantity = cartDao.getPreviousQuantity(username, id);
		newQuantity = previousQuantity - 1;
		if(newQuantity > 0) {
			cartDao.updateArticleQuantity(username, id, newQuantity);
		}
		else {
			cartDao.removeAllArticles(username, id);
		}
		cartList = cartDao.getUserCartList(username);
		cartCounter = cartDao.getUserArticlesNumber(username);
		total = cartDao.getTotal(username);
		session.setAttribute("total", total);
		session.setAttribute("user_articles", cartCounter);
		session.setAttribute("cartList", cartList);
		return "CartPage";
	}
	
	@GetMapping("/removeAll/article/{articleId}")
	public String removeAllArticle(@PathVariable("articleId") int id, HttpSession session) {
		String username = (String) session.getAttribute("loggedUser");
		List<CartArticle> cartList = new ArrayList<>();
		int cartCounter = 0;
		double total = 0;
		cartDao.removeAllArticles(username, id);
		cartList = cartDao.getUserCartList(username);
		cartCounter = cartDao.getUserArticlesNumber(username);
		total = cartDao.getTotal(username);
		session.setAttribute("total", total);
		session.setAttribute("user_articles", cartCounter);
		session.setAttribute("cartList", cartList);
		return "CartPage";
	}
	
	@GetMapping("/buy")
	public String buy(HttpSession session, ModelMap map) {
		String username = (String) session.getAttribute("loggedUser");
		List<CartArticle> cartList = new ArrayList<>();
		cartList = cartDao.getUserCartList(username);
		List<CartArticle> filteredCartList = cartList.stream().filter(article -> article.getQuantity() > article.getAvailability()).collect(Collectors.toList());
		if(filteredCartList.isEmpty()) {
			cartDao.updateArticlesAvailability(username);
			cartDao.buyArticles(username);
			session.removeAttribute("cartList");
			session.setAttribute("user_articles", 0);
			map.addAttribute("purchaseMessage", "Your products have been purchased successfully!");
		}
		else {
			map.addAttribute("articlesAvailabilityMessage", "One or more selected articles are not available. Please remove unavailable products to complete the purchase.");
			session.setAttribute("cartList", cartList);
		}
		return "CartPage";
	}
}
