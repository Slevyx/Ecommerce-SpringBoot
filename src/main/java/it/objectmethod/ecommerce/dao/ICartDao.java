package it.objectmethod.ecommerce.dao;

import java.util.List;

import it.objectmethod.ecommerce.models.CartArticle;

public interface ICartDao {

	public int getUserArticlesNumber(String username);
	
	public void addToCart(String username, int id, int quantity);
	
	public boolean isArticleAlreadyInTheCart(String username, int id);
	
	public double getTotal(String username);

	public List<CartArticle> getUserCartList(String username);
	
	public int getPreviousQuantity(String username, int id);
	
	public void removeAllArticles(String username, int id);

	public void updateArticleQuantity(String username, int id, int quantity);
	
	public void updateArticlesAvailability(String username);
	
	public void buyArticles(String username);
}
