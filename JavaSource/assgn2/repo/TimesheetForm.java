package assgn2.repo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import assgn2.access.TimesheetManager;
import assgn2.model.Employee;
import assgn2.model.Timesheet;
import assgn2.model.TimesheetRow;

@Named
@ConversationScoped
public class TimesheetForm implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Inject private TimesheetManager timesheetManager;
    @Inject private CurrentUser user;
    @Inject private Conversation conversation;
    
    private Timesheet timesheet;

    public Timesheet getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    public String save() {
        timesheetManager.removeTimesheetAllRows(timesheet);

        // check row entry
        for(TimesheetRow row : timesheet.getDetails()) {
            List<String> added = new ArrayList<String>();
            // check and insert single row.
            if(row.getWorkPackage() == null || row.getWorkPackage().isEmpty())
                continue;
            if(added.contains(row.getWorkPackage()+row.getProjectID()))
                continue;
            timesheetManager.addTimesheetAllRows(timesheet, row);
            added.add(row.getWorkPackage()+row.getProjectID());
        }
        
        conversation.end();
        return "timesheets";
    }
    
    public String back() {
        conversation.end();
        return "timesheets";
    }
    
    public String view(Timesheet ts) {
        timesheet = ts;
        if(!conversation.isTransient()) {
            conversation.end();
        }
        conversation.begin();
        return "timesheet";
    }
    public String viewCurrentTimesheet() {
        if(!conversation.isTransient()) {
            conversation.end();
        }
        conversation.begin();
        
        timesheet = timesheetManager.getCurrentTimesheet(user.getEmployee());
        // if current week Time sheet isn't in DB,
        // Insert new empty Time sheet to DB and get currentTimesheet again.
        if(timesheet == null) {
            timesheetManager.addTimesheet();
            timesheet = timesheetManager.getCurrentTimesheet(user.getEmployee());
        }
        for(int i = timesheet.getDetails().size(); i < 5 ; i++)
                timesheet.addRow();
        return "currentTimesheet";
    }
    
    

 
    
    
    
}
