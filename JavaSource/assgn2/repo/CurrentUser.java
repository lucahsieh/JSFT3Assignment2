package assgn2.repo;

import java.io.Serializable;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import assgn2.access.EmployeeManager;
import assgn2.model.Credentials;
import assgn2.model.Employee;

@Named
@SessionScoped
public class CurrentUser implements Serializable{
    private static final long serialVersionUID = 1L;

    private Employee employee;
    private Credentials credential;
    
    @Inject
    private EmployeeManager employeeManager;
    
//    @Inject
//    private Conversation conversation;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Credentials getCredential() {
        if(credential == null)
            credential = new Credentials();
        return credential;
    }

    public void setCredential(Credentials credential) {
        this.credential = credential;
    }
    
    public String login() {
        boolean result = employeeManager.verifyUser(credential);
        if(result) {
            employee = employeeManager.getCurrentEmployee(credential.getUserName());
            return "timesheetList"; //next page
        }else {
            employee = null;
        }
        return "index";
    }

}
