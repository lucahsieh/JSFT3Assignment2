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
@SessionScoped
public class TimesheetForm implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Inject private TimesheetManager timesheetManager;
    @Inject private TimesheetListForm list;
    @Inject private Conversation conversation;
        
    private List<EditableTimesheetRow> rows;
    private BigDecimal total;
    private BigDecimal[] totalForWeek;
    
    public void refreshRows() {
        List<TimesheetRow> dbRows = list.getSelected().getDetails();
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
        return list.getSelected().getOvertime();
    }

    public BigDecimal getFlextime() {
        return list.getSelected().getFlextime();
    }
    
      public Date getEndWeek() {
          return list.getSelected().getEndWeek();
      }

    public List<EditableTimesheetRow> getRows() {
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
        return list.getSelected().getWeekNumber();
    }
    
    public String save() {
        for (EditableTimesheetRow er : rows) {
            if (er.isEditable()) {
//                productManager.merge(e.getProduct());
                er.setEditable(false);
            }
        }
//        conversation.end();
        return "timesheetList";
    }
    
    public String back() {
//        conversation.end();
        return "timesheetList";
    }
    

 
    
    
    
}
