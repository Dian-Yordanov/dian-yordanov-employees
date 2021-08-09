import java.util.ArrayList;
import java.util.Date;

public class EmployeeCollaboration {

    private String employeesCollaboration = null;
    private ArrayList<String> projectBeingWorkedOn = new ArrayList<>();
    private ArrayList<Long> periodBeingWorkedOn = new ArrayList<>();

    public EmployeeCollaboration(String employeesCollaboration, String projectBeingWorkedOn, Long periodBeingWorkedOn){
        this.employeesCollaboration = employeesCollaboration;
        this.projectBeingWorkedOn.add(projectBeingWorkedOn);
        this.periodBeingWorkedOn.add(periodBeingWorkedOn);
    }

    public String getemployeesCollaboration()
    {
        return employeesCollaboration;
    }
    public ArrayList<String> getprojectBeingWorkedOn()
    {
        return projectBeingWorkedOn;
    }
    public ArrayList<Long> getperiodBeingWorkedOn()
    {
        return periodBeingWorkedOn;
    }

    public void addProjectsworkedOn(String projectBeingWorkedOn)
    {
        this.projectBeingWorkedOn.add(projectBeingWorkedOn);
    }

    public void addPeriodBeingWorkedOn(Long periodBeingWorkedOn)
    {
        this.periodBeingWorkedOn.add(periodBeingWorkedOn);
    }

}

