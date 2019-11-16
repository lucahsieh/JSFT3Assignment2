package assgn2.access;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.sql.DataSource;

import assgn2.model.Employee;
import assgn2.model.Timesheet;
import assgn2.model.TimesheetRow;
import assgn2.repo.TimesheetCollection;

@Named
@SessionScoped
public class TimesheetManager implements TimesheetCollection, Serializable{
    private static final long serialVersionUID = 1L;

    @Resource(mappedName = "java:jboss/datasources/timesheetDB")
    private DataSource ds;
    
    @Override
    public List<Timesheet> getTimesheets() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Timesheet> getTimesheets(Employee e) {
        ArrayList<Timesheet> timesheets = new ArrayList<Timesheet>();
        Connection connection = null;
        Statement stmt = null;
        try {
            try {
                connection = ds.getConnection();
                try {
                    stmt = connection.createStatement();
                    ResultSet result = stmt.executeQuery(
                            "SELECT * FROM Timesheets where EmpNumber = '"
                                    + e.getEmpNumber() + "'");
                    while (result.next()) {
                        Date endWeek = result.getDate("EndWeek");
                        timesheets.add(
                                new Timesheet( 
                                    e,
                                    endWeek,
                                    getTimesheetRows(e.getEmpNumber(),endWeek)
                                ));
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
            System.out.println("Error in getTimesheets " + e);
            ex.printStackTrace();
            return null;
        }
        return timesheets;
    }
    
    public List<TimesheetRow> getTimesheetRows(int empNumber, Date endWeek){
        ArrayList<TimesheetRow> rows = new ArrayList<TimesheetRow>();
        Connection connection = null;
        Statement stmt = null;
        try {
            try {
                connection = ds.getConnection();
                try {
                    stmt = connection.createStatement();
                    ResultSet result = stmt.executeQuery(
                            "SELECT * FROM TimesheetRows where EmpNumber = '"
                                    + empNumber + "' AND EndWeek = '"+ endWeek +"'");
                    while (result.next()) {
                        BigDecimal[] hours = {
                                result.getBigDecimal("Sat"),
                                result.getBigDecimal("Sun"),
                                result.getBigDecimal("Mon"),
                                result.getBigDecimal("Tue"),
                                result.getBigDecimal("Wed"),
                                result.getBigDecimal("Thu"),
                                result.getBigDecimal("Fri")
                                };
                        rows.add(
                                new TimesheetRow( 
                                    result.getInt("ProjectID"),
                                    result.getString("WorkPackage"),
                                    hours,
                                    result.getString("Notes")
                                ));
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
            System.out.println("Error in getTimesheetRows " + empNumber + " " + endWeek);
            ex.printStackTrace();
            return null;
        }
        return rows;
    }

    @Override
    public Timesheet getCurrentTimesheet(Employee e) {
        Connection connection = null;
        Statement stmt = null;
        
        Calendar c = new GregorianCalendar();
        int currentDay = c.get(Calendar.DAY_OF_WEEK);
        int leftDays = Calendar.FRIDAY - currentDay;
        c.add(Calendar.DATE, leftDays);
        Date endWeek = c.getTime();

        try {
            try {
                connection = ds.getConnection();
                try {
                    stmt = connection.createStatement();
                    ResultSet result = stmt.executeQuery(
                            "SELECT * FROM Timesheets where EmpNumber = '"
                                    + e.getEmpNumber() + "' EndWeek = '" + endWeek + "'");
                    if (result.next()) {
                        return new Timesheet( 
                                    e,
                                    endWeek,
                                    getTimesheetRows(e.getEmpNumber(),endWeek)
                                );
                    }else
                        return null;
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
            System.out.println("Error in getTimesheets " + e);
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public String addTimesheet() {
        // TODO Auto-generated method stub
        return null;
    }

}
