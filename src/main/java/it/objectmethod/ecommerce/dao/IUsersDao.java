package it.objectmethod.ecommerce.dao;

import it.objectmethod.ecommerce.models.User;

public interface IUsersDao {
	
	public User getUser(String username, String password);
}
