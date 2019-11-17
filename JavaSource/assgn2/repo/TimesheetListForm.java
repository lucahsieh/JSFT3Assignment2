package assgn2.repo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import assgn2.access.EmployeeManager;
import assgn2.access.TimesheetManager;
import assgn2.model.Timesheet;

@Named
@SessionScoped
public class TimesheetListForm implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Inject private CurrentUser user;
    @Inject private TimesheetManager timesheetManager;
    @Inject private Conversation conversation;
    
    private List<Timesheet> list;
    private Timesheet selected;
    
    private void refreshList() {
        list = timesheetManager.getTimesheets(user.getEmployee());
    }
    
private String peterName;
    
    public String getPeterName() {
        return "peter";
    }

    public List<Timesheet> getList() {
//        if(list == null) {
//            refreshList();
//        }
        refreshList();
        return list;
    }

    public void setList(List<Timesheet> list) {
        this.list = list;
    }

    public Timesheet getSelected() {
        return selected;
    }

//    public void setSelectedTimesheet(Timesheet selected) {
//        this.selected = selected;
//    }
    

    public boolean isCurrentTimesheet(Timesheet ts) {
        Calendar c = new GregorianCalendar();
        int currentDay = c.get(Calendar.DAY_OF_WEEK);
        int leftDays = Calendar.FRIDAY - currentDay;
        c.add(Calendar.DATE, leftDays);
        Date endWeek = c.getTime();
        Date TimesheetDate = ts.getEndWeek();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
//        String d1 = 
        return formatter.format(endWeek).equals(formatter.format(TimesheetDate)) ;
    }

}
