package org.example.jdbctemplatetesting.dao;

import lombok.SneakyThrows;
import org.example.jdbctemplatetesting.ds.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EmployeeDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Employee> findAllEmployees() {
        String sql = "SELECT * FROM employee";
        return jdbcTemplate.query(sql, new RowMapper<Employee>() {
            @Override
            public Employee mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                return new Employee(resultSet.getInt("employee_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number"),
                        resultSet.getDate("hire_date"),
                        resultSet.getInt("salary"));
            }
        });
    }

    @SneakyThrows
    public Employee mapEmployee(ResultSet resultSet, int i) {
        return new Employee(
                resultSet.getInt("employee_id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("email"),
                resultSet.getString("phone_number"),
                resultSet.getDate("hire_date"),
                resultSet.getInt("salary")
        );
    }

    public static class AverageSalariesRowCallBackHandler implements RowCallbackHandler {

        private float salarySum;
        private int salaryCount;
        @Override
        public void processRow(ResultSet rs) throws SQLException {
            salarySum += rs.getFloat("salary");
            salaryCount ++;
        }

        public Float getAverageSalary() {
            return salarySum/(float) salaryCount;
        }

    }
    public Float findAverageSalary() {
        AverageSalariesRowCallBackHandler averageSalariesRowCallBackHandler=
                new AverageSalariesRowCallBackHandler();
        String sql = "Select salary from employee";
        jdbcTemplate.query(sql, averageSalariesRowCallBackHandler);
        return averageSalariesRowCallBackHandler.getAverageSalary();
    }

    public static class AverageSalaryResultSetExtractor implements ResultSetExtractor {


        @Override
        public Float extractData(ResultSet rs) throws SQLException, DataAccessException {
            float salarySum = 0;
            int salaryCount = 0;
            while(rs.next()) {
                salarySum += rs.getFloat("salary");
                salaryCount++;
            }
            return salarySum/(float)salaryCount;
        }
    }
}
