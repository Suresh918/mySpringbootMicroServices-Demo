package com.example.mirai.libraries.jdbcdatasource.config;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSourceConfiguration {
	/**
	 * JDBC Url to connect to Hana, example jdbc:sap://host:port/?autocommit=false"
	 */
	private String url;

	/**
	 * Username to connect to Hana, user must have access to all the tables/view being queried"
	 */
	private String username;

	/**
	 * Password for the user being used to connect to Hana"
	 */
	private String password;

	/**
	 * Maximum number of connections in the pool, default value is 2"
	 */
	private Integer maximumPoolSize = 10;
}
