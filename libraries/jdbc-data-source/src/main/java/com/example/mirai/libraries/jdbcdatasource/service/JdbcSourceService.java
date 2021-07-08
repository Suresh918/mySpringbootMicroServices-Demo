package com.example.mirai.libraries.jdbcdatasource.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.example.mirai.libraries.jdbcdatasource.exception.JdbcSourceException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public abstract class JdbcSourceService<E> {

	protected JdbcClient jdbcClient;

	public JdbcSourceService(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public abstract JdbcSourceService getSelf();

	public abstract Class<E> getEntityClass();

	public abstract E convertResultSet(ResultSet resultSet);

	public abstract String getSqlForSingle(String id);

	public E getEntity(String id) {
		ResultSet resultSet;
		resultSet = jdbcClient.executeQuery(getSqlForSingle(id) + " limit 1");
		try {
			if (resultSet != null && resultSet.next()) {
				return convertResultSet(resultSet);
			}
		} catch (SQLException sqlException) {
			log.error("Unable to get entity " + id + " : " + sqlException.getMessage());
			throw new JdbcSourceException(sqlException.getMessage());
		}
		return null;
	}

	public List<E> getEntities(List<String> ids) {
		return (List<E>) ids.stream().map(id -> getSelf().getEntity(id)).filter(Objects::nonNull).collect(Collectors.toList());
	}

}
