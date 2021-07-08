package com.example.mirai.libraries.jdbcdatasource.service;

import java.sql.*;

import com.example.mirai.libraries.jdbcdatasource.config.DataSourceConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@Slf4j
public abstract class JdbcClient {

	private HikariDataSource dataSource;
	private static HikariConfig config = new HikariConfig();
	private DataSourceConfiguration dataSourceConfiguration;

	public JdbcClient(DataSourceConfiguration dataSourceConfiguration) {
		this.dataSourceConfiguration = dataSourceConfiguration;

		config.setJdbcUrl( dataSourceConfiguration.getUrl() );
		config.setUsername( dataSourceConfiguration.getUsername() );
		config.setPassword( dataSourceConfiguration.getPassword() );
		config.setMaximumPoolSize(dataSourceConfiguration.getMaximumPoolSize());

		dataSource = new HikariDataSource(config);
	}

	public ResultSet executeQuery(String query) {
		try {
			Connection connection =  dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			return resultSet;
		} catch (Exception exception) {
			log.error("Unable to execute query: " + query + exception.getMessage());
			exception.printStackTrace();
			return null;
		}
	}
}
