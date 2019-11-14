package assgn2.repo;

import java.io.Serializable;
import java.util.List;

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
    
    @Inject
    private CurrentUser user;
    
    @Inject
    private EmployeeManager employeeManager;
    
    @Inject
    private TimesheetManager timesheetManager;
    
    List<Timesheet> list;
    
    private void refreshList() {
        list = timesheetManager.getTimesheets(user.getEmployee());
    }

    public List<Timesheet> getList() {
        if(list == null) {
            refreshList();
        }
        return list;
    }

    public void setList(List<Timesheet> list) {
        this.list = list;
    }

}
