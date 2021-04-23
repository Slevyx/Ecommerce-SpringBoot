package it.objectmethod.ecommerce.dao;

import java.util.List;

import it.objectmethod.ecommerce.models.Article;

public interface IArticlesDao {

	public List<Article> getArticles();
}
