import java.util.Date;

public class Employee {

    private String EmployeeID = null;
    private String ProjectID = null;
    private Date DateFrom = null;
    private Date DateTo = null;

    public Employee(String EmployeeID, String ProjectID, Date DateFrom, Date DateTo){
        this.EmployeeID = EmployeeID;
        this.ProjectID = ProjectID;
        this.DateFrom = DateFrom;
        this.DateTo = DateTo;
    }
    public void setProjectID(String ProjectID)
    {
        this.ProjectID = ProjectID;
    }
    public String getProjectID()
    {
        return ProjectID;
    }
    public void setDateFrom(Date DateFrom)
    {
        this.DateFrom = DateFrom;
    }
    public Date getDateFrom()
    {
        return DateFrom;
    }
    public void setDateTo(Date DateTo)
    {
        this.DateTo = DateTo;
    }
    public Date getDateTo()
    {
        return DateTo;
    }


}
