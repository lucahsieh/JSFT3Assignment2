package assgn2.repo;

import java.math.BigDecimal;

import assgn2.model.TimesheetRow;

public class EditableTimesheetRow{

    private boolean editable;
    private TimesheetRow row;
    private BigDecimal rowTotal;
    
    public EditableTimesheetRow(TimesheetRow row) {
        this.row = row;
        this.rowTotal = new BigDecimal(0);
        for(BigDecimal hr : row.getHoursForWeek()) {
            this.rowTotal = this.rowTotal.add(hr);
        }
    }
    
    
    public boolean isEditable() {
        return editable;
    }
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    public TimesheetRow getRow() {
        return row;
    }
    public void setRow(TimesheetRow row) {
        this.row = row;
    }
    public BigDecimal getRowTotal() {
        return rowTotal;
    }
    public void setRowTotal(BigDecimal rowTotal) {
        this.rowTotal = rowTotal;
    }
    
    
}
