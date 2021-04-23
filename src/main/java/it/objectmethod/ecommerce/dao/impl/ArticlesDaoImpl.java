package it.objectmethod.ecommerce.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.objectmethod.ecommerce.dao.IArticlesDao;
import it.objectmethod.ecommerce.models.Article;

@Component
public class ArticlesDaoImpl implements IArticlesDao{
	
	@Autowired
	DataSource dataSource;

	@Override
	public List<Article> getArticles() {
		List<Article> articlesList = new ArrayList<>();
		String sqlQuery = "SELECT * FROM articolo";
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()) {
				Article article = new Article();
				article.setId(resultSet.getInt("id_articolo"));
				article.setCode(resultSet.getString("codice_articolo"));
				article.setName(resultSet.getString("nome_articolo"));
				article.setAvailability(resultSet.getInt("disponibilita"));
				article.setPrice(resultSet.getDouble("prezzo_unitario"));
				articlesList.add(article);
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return articlesList;
	}

}
