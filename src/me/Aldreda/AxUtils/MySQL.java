package me.Aldreda.AxUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
	private final Connection connection;
	
	MySQL(String host, int port, String database, String username, String password) throws SQLException {
		this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false",username,password);
	}
	
	public final Connection getConnection() {
		return connection;
	}
}