package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager
{
	private static final String	DB_URL		= "jdbc:mysql://localhost/db_client";
	private static final String	DB_USER		= "root";
	private static final String	DB_PASSWORD	= "";

	private static Connection	connexion;

	private DatabaseManager()
	{
	}

	public static Connection getConnection() throws DaoException
	{
		if (connexion == null)
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				Connection connexion = DriverManager.getConnection(DB_URL,
						DB_USER, DB_PASSWORD);
				return connexion;
			}
			catch (ClassNotFoundException e)
			{
				throw new DaoException("Erreur du chargement du pilote");
			}
			catch (SQLException e)
			{
				throw new DaoException("Erreur d'accès à la base de données : "
						+ e.getMessage());
			}
		}
		return connexion;
	}
}
