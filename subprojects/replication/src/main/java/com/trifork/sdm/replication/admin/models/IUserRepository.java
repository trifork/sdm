package com.trifork.sdm.replication.admin.models;


import static com.trifork.sdm.replication.db.properties.Database.*;

import java.sql.SQLException;
import java.util.List;

import com.google.inject.ImplementedBy;
import com.trifork.sdm.replication.db.properties.Transactional;


@ImplementedBy(UserRepository.class)
public interface IUserRepository
{
	@Transactional(ADMINISTRATION)
	User find(String id) throws SQLException;


	@Transactional(ADMINISTRATION)
	User create(String name, String cpr, String cvr) throws SQLException;


	@Transactional(ADMINISTRATION)
	List<User> findAll() throws SQLException;


	@Transactional(ADMINISTRATION)
	void destroy(String id) throws SQLException;


	@Transactional(ADMINISTRATION)
	boolean isAdmin(String userCPR, String userCVR) throws SQLException;

}
