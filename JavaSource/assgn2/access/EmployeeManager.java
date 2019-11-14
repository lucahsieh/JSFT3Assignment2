package assgn2.access;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import assgn2.model.Credentials;
import assgn2.model.Employee;
import assgn2.repo.CurrentUser;
import assgn2.repo.EmployeeList;

@ApplicationScoped
public class EmployeeManager implements Serializable, EmployeeList{
    private static final long serialVersionUID =1L;

    @Resource(mappedName = "java:jboss/datasources/timesheetDB")
    private DataSource ds;

    @Override
    public List<Employee> getEmployees() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Employee getEmployee(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getLoginCombos() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Employee getCurrentEmployee() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Employee getCurrentEmployee(String userName) {
        Statement stmt = null;
        Connection connection = null;
        try {
            try {
                connection = ds.getConnection();
                try {
                    stmt = connection.createStatement();
                    ResultSet result = stmt
                            .executeQuery("SELECT * FROM Employees "
                                    + "where UserName = '" + userName + "'");
                    if (result.next()) {
                        return new Employee(
                                        result.getString("Name"),
                                        result.getInt("EmpNumber"),
                                        result.getString("UserName"));
                    } else {
                        return null;
                    }
                } finally {
                    if (stmt != null) {
                        stmt.close();
                    }

                }
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in getCurrentEmployee " + userName);
            ex.printStackTrace();
            return null;
        }
        
    }

    @Override
    public Employee getAdministrator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean verifyUser(Credentials credential) {
        Statement stmt = null;
        Connection connection = null;
        try {
            try {
                connection = ds.getConnection();
                try {
                    stmt = connection.createStatement();
                    ResultSet result = stmt
                            .executeQuery("SELECT * FROM Credentials "
                                    + "where UserName = '" + credential.getUserName() + "'");
                    if (result.next()) {
                        return credential.getPassword().equals(result.getString("Password"));
                    } else {
                        return false;
                    }
                } finally {
                    if (stmt != null) {
                        stmt.close();
                    }

                }
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in verifyUser " + credential);
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public String logout(Employee employee) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteEmployee(Employee userToDelete) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addEmployee(Employee newEmployee) {
        // TODO Auto-generated method stub
        
    }

}
