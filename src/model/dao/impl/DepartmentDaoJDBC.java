package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	Connection connection = null;

	public DepartmentDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement statement = null;
		ResultSet result = null;
		
		try {

			statement = connection.prepareStatement("INSERT INTO department (Name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, obj.getName());
			int rows = statement.executeUpdate();

			if (rows > 0) {
				result = statement.getGeneratedKeys();
				if (result.next()) {
					obj.setId(result.getInt(1));
				}
			}else {
				throw new DbException("Unexpected error! No rows affected!");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
		}

	}

	@Override
	public void update(Department obj) {
		PreparedStatement statement = null;

		try {

			statement = connection.prepareStatement("UPDATE department SET Name = ? WHERE Id = ?");
			statement.setString(1, obj.getName());
			statement.setInt(2, obj.getId());

			statement.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
		}

	}

	@Override
	public void deleteById(Integer id) {

		PreparedStatement statement = null;

		try {

			statement = connection.prepareStatement("DELETE FROM department WHERE Id = ?");
			statement.setInt(1, id);

			statement.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
		}

	}

	@Override
	public Department findById(Integer id) {

		PreparedStatement statement = null;
		ResultSet result = null;

		try {

			statement = connection.prepareStatement("SELECT * FROM department WHERE Id = ?");
			statement.setInt(1, id);
			result = statement.executeQuery();

			if (result.next()) {
				return new Department(result.getInt("Id"), result.getString("Name"));
			} else {
				return null;
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
		}
	}

	@Override
	public List<Department> findAll() {

		PreparedStatement statement = null;
		ResultSet result = null;

		try {

			statement = connection.prepareStatement("SELECT * FROM department ORDER BY Name");

			result = statement.executeQuery();

			List<Department> departments = new ArrayList<>();

			while (result.next()) {
				departments.add(new Department(result.getInt("Id"), result.getString("Name")));
			}
			return departments;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
		}
	}

}
