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
	public String loadShopPage(HttpSession session, ModelMap map) {
		String username = (String) session.getAttribute("loggedUser");
		String forwardTo = "redirect:/login";
		int cartCounter = 0;
		if(username != null) {
			forwardTo = "ShopPage";
			cartCounter = cartDao.getUserArticlesNumber(username);
			map.addAttribute("user_articles", cartCounter);
		}
		return forwardTo;
	}
	
	@GetMapping("/addToCart/article/{articleId}")
	public String addToCart(@PathVariable("articleId") int id, HttpSession session, ModelMap map) {
		String username = (String) session.getAttribute("loggedUser");
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
		return "redirect:/articles";
	}
	
	@GetMapping("/cart")
	public String getTotal(HttpSession session, ModelMap map) {
		String username = (String) session.getAttribute("loggedUser");
		List<CartArticle> cartList = new ArrayList<>();
		double total = 0;
		String forwardTo = "redirect:/login";
		if(username != null) {
			forwardTo = "CartPage";
			cartList = cartDao.getUserCartList(username);
			total = cartDao.getTotal(username);
			map.addAttribute("cartList", cartList);
			map.addAttribute("total", total);
		}
		return forwardTo;
	}
	
	@GetMapping("/addOne/article/{articleId}")
	public String addOneArticle(@PathVariable("articleId") int id, HttpSession session) {
		String username = (String) session.getAttribute("loggedUser");
		int previousQuantity = 0;
		previousQuantity = cartDao.getPreviousQuantity(username, id);
		cartDao.updateArticleQuantity(username, id, previousQuantity + 1);
		return "redirect:/cart";
	}
	
	@GetMapping("/removeOne/article/{articleId}")
	public String removeOneArticle(@PathVariable("articleId") int id, HttpSession session) {
		String username = (String) session.getAttribute("loggedUser");
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
		return "redirect:/cart";
	}
	
	@GetMapping("/removeAll/article/{articleId}")
	public String removeAllArticle(@PathVariable("articleId") int id, HttpSession session) {
		String username = (String) session.getAttribute("loggedUser");
		cartDao.removeAllArticles(username, id);
		return "redirect:/cart";
	}
	
	@GetMapping("/cart/buy")
	public String buy(HttpSession session, ModelMap map) {
		String username = (String) session.getAttribute("loggedUser");
		String forwardTo = "redirect:/login";
		if(username != null) {
			forwardTo = "CartPage";
			List<CartArticle> cartList = new ArrayList<>();
			cartList = cartDao.getUserCartList(username);
			List<CartArticle> filteredCartList = cartList.stream().filter(article -> article.getQuantity() > article.getAvailability()).collect(Collectors.toList());
			if(filteredCartList.isEmpty()) {
				cartDao.updateArticlesAvailability(username);
				cartDao.buyArticles(username);
				map.addAttribute("purchaseMessage", "Your products have been purchased successfully!");
			}
			else {
				map.addAttribute("articlesAvailabilityMessage", "One or more selected articles are not available. Please remove unavailable products to complete the purchase.");
				map.addAttribute("cartList", cartList);
			}
		}
		return forwardTo;
	}
}
