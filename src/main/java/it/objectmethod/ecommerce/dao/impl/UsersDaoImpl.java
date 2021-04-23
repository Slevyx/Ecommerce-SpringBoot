package it.objectmethod.ecommerce.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.objectmethod.ecommerce.dao.IUsersDao;
import it.objectmethod.ecommerce.models.User;

@Component
public class UsersDaoImpl implements IUsersDao{

	@Autowired
	DataSource dataSource;
	
	@Override
	public User getUser(String username, String password) {
		User user = null;
		String sqlQuery = "SELECT * FROM utente u WHERE u.nome_utente = ? AND u.password = ?";
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()) {
				user = new User();
				user.setUsername(resultSet.getString("nome_utente"));
				user.setPassword(resultSet.getString("password"));
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
}
