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
//    @Inject private TimesheetListForm list;
    @Inject private Conversation conversation;
    
    private Timesheet timesheet;
        
    private List<EditableTimesheetRow> rows;
    private BigDecimal total;
    private BigDecimal[] totalForWeek;
    
    public TimesheetForm() {
        this.total = this.total;

    }
    
    public void refreshRows() {
        List<TimesheetRow> dbRows = timesheet.getDetails();
        rows = new ArrayList<EditableTimesheetRow>();
        for(int i = 0; i < dbRows.size(); i++) {
            rows.add(new EditableTimesheetRow(dbRows.get(i)));
        }
    }
    
    private void refreshColumnTotal() {
        total = new BigDecimal(0);
        totalForWeek = new BigDecimal[7];
        for(int i = 0 ; i < totalForWeek.length; i++) {
            totalForWeek[i] = new BigDecimal(0);
        }
        for(EditableTimesheetRow eRow: rows) {
            total = total.add(eRow.getRowTotal());
            for(int i = 0; i < 7 ; i ++) {
                BigDecimal hr = eRow.getRow().getHour(i);
                totalForWeek[i] = totalForWeek[i].add(hr);
            }
        }
    }

    public BigDecimal getOvertime() {
        return timesheet.getOvertime();
    }

    public BigDecimal getFlextime() {
        return timesheet.getFlextime();
    }
    
      public Date getEndWeek() {
          return timesheet.getEndWeek();
      }

    public List<EditableTimesheetRow> getRows() {
        if(rows == null)
            refreshRows();
        return rows;
    }

    public void setRows(List<EditableTimesheetRow> rows) {
        this.rows = rows;
    }

    public BigDecimal getTotal() {
        refreshColumnTotal();
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal[] getTotalForWeek() {
        refreshColumnTotal();
        return totalForWeek;
    }

    public void setTotalForWeek(BigDecimal[] totalForWeek) {
        this.totalForWeek = totalForWeek;
    }
    
    public int getWeekNumber() {
        return timesheet.getWeekNumber();
    }
    
    public Timesheet getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    public String save() {
        for (EditableTimesheetRow er : rows) {
            if (er.isEditable()) {
//                productManager.merge(e.getProduct());
                er.setEditable(false);
            }
        }
        conversation.end();
        return "timesheetList";
    }
    
    public String back() {
//        refreshRows();
        conversation.end();
        return "timesheetList";
    }
    
    public String view(Timesheet ts) {
        timesheet = ts;
        if(!conversation.isTransient()) {
            conversation.end();
        }
        conversation.begin();
        return "timesheet";
    }
    

 
    
    
    
}
