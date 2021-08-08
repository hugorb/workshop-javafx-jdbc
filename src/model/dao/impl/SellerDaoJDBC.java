package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection connection;

	public SellerDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement statement = null;

		try {

			statement = connection.prepareStatement(
					"INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, obj.getName());
			statement.setString(2, obj.getEmail());
			statement.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			statement.setDouble(4, obj.getBaseSalary());
			statement.setInt(5, obj.getDepartment().getId());

			int rowsAfected = statement.executeUpdate();

			if (rowsAfected > 0) {
				ResultSet rs = statement.getGeneratedKeys();
				if (rs.next()) {
					obj.setId(rs.getInt(1));
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Unexpeted ERROR! no Rows affected!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
		}

	}

	@Override
	public void update(Seller obj) {
		PreparedStatement statement = null;

		try {

			statement = connection.prepareStatement("UPDATE seller SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? WHERE Id = ?");
			statement.setString(1, obj.getName());
			statement.setString(2, obj.getEmail());
			statement.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			statement.setDouble(4, obj.getBaseSalary());
			statement.setInt(5, obj.getDepartment().getId());
			statement.setInt(6, obj.getId());

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
			statement = connection.prepareStatement("DELETE FROM seller WHERE Id = ?");
			statement.setInt(1, id);
			statement.executeUpdate();
		}catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		

	}

	@Override
	public Seller findById(Integer id) {

		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			statement = connection.prepareStatement(
					"SELECT seller.*,department.Name as DepName FROM seller INNER JOIN department ON seller.DepartmentId = department.Id WHERE seller.Id = ?");
			statement.setInt(1, id);
			result = statement.executeQuery();
			if (result.next()) {
				Department department = new Department(result.getInt("DepartmentID"), result.getString("DepName"));
				Seller seller = new Seller(result.getInt("Id"), result.getString("Name"), result.getString("Email"),
						result.getDate("BirthDate"), result.getDouble("BaseSalary"), department);
				return seller;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
		}
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement statement = null;
		ResultSet result = null;

		try {

			statement = connection.prepareStatement(
					"SELECT seller.*,department.Name as DepName FROM seller INNER JOIN department ON seller.DepartmentId = department.Id ORDER BY Name");

			result = statement.executeQuery();

			List<Seller> sellerList = new ArrayList<>();

			Set<Department> departments = new HashSet<>();

			while (result.next()) {

				Department newDepartment = new Department(result.getInt("DepartmentID"), result.getString("DepName"));
				departments.add(newDepartment);

				for (Department department : departments) {
					if (department.equals(newDepartment)) {
						sellerList.add(
								new Seller(result.getInt("Id"), result.getString("Name"), result.getString("Email"),
										result.getDate("BirthDate"), result.getDouble("BaseSalary"), department));
					}
				}
			}
			return sellerList;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
		}
	}

	@Override
	public List<Seller> findByDepertment(Department department) {
		PreparedStatement statement = null;
		ResultSet result = null;

		try {

			statement = connection.prepareStatement(
					"SELECT seller.*,department.Name as DepName FROM seller INNER JOIN department ON seller.DepartmentId = department.Id WHERE DepartmentId = ? ORDER BY Name");
			statement.setInt(1, department.getId());
			result = statement.executeQuery();

			List<Seller> sellerList = new ArrayList<>();

			Department dep = null;

			while (result.next()) {
				if (dep == null) {
					dep = new Department(result.getInt("DepartmentID"), result.getString("DepName"));
				}
				sellerList.add(new Seller(result.getInt("Id"), result.getString("Name"), result.getString("Email"),
						result.getDate("BirthDate"), result.getDouble("BaseSalary"), dep));
			}
			return sellerList;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
		}
	}

}
