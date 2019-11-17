package assgn2.access;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import assgn2.model.Employee;
import assgn2.model.Timesheet;
import assgn2.model.TimesheetRow;
import assgn2.repo.CurrentUser;
import assgn2.repo.TimesheetCollection;

@Named
@SessionScoped
public class TimesheetManager implements TimesheetCollection, Serializable{
    private static final long serialVersionUID = 1L;
    
    @Inject CurrentUser user;

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
                                    + e.getEmpNumber() + "' ORDER BY EndWeek DESC");
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
                                    + empNumber + "' AND EndWeek = '"+ java.sql.Date.valueOf(DateToString(endWeek)) +"'");
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
        Date endWeek = getCurrentEndWeek();
       
        try {
            try {
                connection = ds.getConnection();
                try {
                    stmt = connection.createStatement();
                    ResultSet result = stmt.executeQuery(
                            "SELECT * FROM Timesheets where EmpNumber = '"
                                    + e.getEmpNumber() + "' AND EndWeek = '" + DateToString(endWeek) + "'");
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
      //order of fields in INSERT statement
        final int EmpNumber = 1;
        final int EndWeek = 2;
        final int OverTime = 3;
        final int FlexTime = 4;
        
        Date endWeek = getCurrentEndWeek();
        Timesheet newTs = new Timesheet();
        newTs.setEmployee(user.getEmployee());
        newTs.setEndWeek(endWeek);
        newTs.setFlextime(new BigDecimal(0));
        newTs.setOvertime(new BigDecimal(0));
        
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            try {
                connection = ds.getConnection();
                try {
                    stmt = connection
                            .prepareStatement("INSERT INTO timesheets "
                                    + "VALUES (?, ?, ?, ?)");
                    stmt.setInt(EmpNumber, newTs.getEmployee().getEmpNumber());
                    stmt.setDate(EndWeek, java.sql.Date.valueOf(DateToString(endWeek)));
                    stmt.setInt(OverTime, newTs.getOvertime().intValue());
                    stmt.setInt(FlexTime, newTs.getFlextime().intValue());
                    stmt.executeUpdate();
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
            System.out.println("Error in addTimesheet " + newTs.getEmployee().getEmpNumber() + newTs.getEndWeek().toString());
            ex.printStackTrace();
        }
        return null;
    }
    
    public void removeTimesheetAllRows(Timesheet ts) {
        final int EmpNumber = 1;
        final int EndWeek = 2;
        
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            try {
                connection = ds.getConnection();
                try {
                    stmt = connection.prepareStatement(
                            "DELETE FROM TimesheetRows WHERE EmpNumber =  ? AND EndWeek = ?");
                    stmt.setInt(EmpNumber, ts.getEmployee().getEmpNumber());
                    stmt.setDate(EndWeek, java.sql.Date.valueOf(DateToString(ts.getEndWeek())) );
                    stmt.executeUpdate();
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
            System.out.println("Error in remove " + ts.getEmployee().getEmpNumber() + ts.getEndWeek().toString());
            ex.printStackTrace();
        }
    }
    public void addTimesheetAllRows(Timesheet ts, TimesheetRow row) {
      //order of fields in INSERT statement
        final int EmpNumber = 1;
        final int EndWeek = 2;
        final int ProjectID = 3;
        final int WorkPackage = 4;
        final int Sat = 5;
        final int Sun = 6;
        final int Mon = 7;
        final int Tue = 8;
        final int Wed = 9;
        final int Thu = 10;
        final int Fri = 11;
        final int Notes = 12;
                
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            try {
                connection = ds.getConnection();
                try {
                    stmt = connection
                            .prepareStatement("INSERT INTO TimesheetRows "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    
                    stmt.setInt(EmpNumber, ts.getEmployee().getEmpNumber());
                    stmt.setDate(EndWeek, java.sql.Date.valueOf(DateToString(ts.getEndWeek())) );
                    stmt.setInt(ProjectID, row.getProjectID());
                    stmt.setString(WorkPackage, row.getWorkPackage());
                    stmt.setInt(Sat, row.getHour(TimesheetRow.SAT) == null ? 0 : row.getHour(TimesheetRow.SAT).intValue());
                    stmt.setInt(Sun, row.getHour(TimesheetRow.SUN) == null ? 0 : row.getHour(TimesheetRow.SUN).intValue());
                    stmt.setInt(Mon, row.getHour(TimesheetRow.MON) == null ? 0 : row.getHour(TimesheetRow.MON).intValue());
                    stmt.setInt(Tue, row.getHour(TimesheetRow.TUE) == null ? 0 : row.getHour(TimesheetRow.TUE).intValue());
                    stmt.setInt(Wed, row.getHour(TimesheetRow.WED) == null ? 0 : row.getHour(TimesheetRow.WED).intValue());
                    stmt.setInt(Thu, row.getHour(TimesheetRow.THU) == null ? 0 : row.getHour(TimesheetRow.THU).intValue());
                    stmt.setInt(Fri, row.getHour(TimesheetRow.FRI) == null ? 0 : row.getHour(TimesheetRow.FRI).intValue());
                    stmt.setString(Notes, row.getNotes());
                    stmt.executeUpdate();
                    
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
            System.out.println("Error in addTimesheetAllRows " + ts.getEmployee().getEmpNumber() + ts.getEndWeek().toString());
            ex.printStackTrace();
        }
    }
    
    
    private Date getCurrentEndWeek() {
        Calendar c = new GregorianCalendar();
        int currentDay = c.get(Calendar.DAY_OF_WEEK);
        int leftDays = Calendar.FRIDAY - currentDay;
        c.add(Calendar.DATE, leftDays);
        Date endWeek = c.getTime();
        return endWeek;
    }
    
    private String DateToString(Date d) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
        return formatter.format(d);  
    }

}
