import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Project {

//    private Integer ProjectID = null;
//    HashMap<Integer, String> listToAddProjects = null;
//    ArrayList<Date> listToAddDates = null;

    private Integer i = null;
    private String ProjectID = null;
    private ArrayList<Date> listToAddDates = null;

    public Project(Integer i, String ProjectID, ArrayList<Date> listToAddDates){
        this.i = i;
        this.ProjectID = ProjectID;
        this.listToAddDates = listToAddDates;

    }

    public Integer getI()
    {
        return this.i;
    }

    public String getProjectID()
    {
        return this.ProjectID;
    }

    public ArrayList<Date> getArrayList()
    {
        return this.listToAddDates;
    }

//
//    public String getProjectID()
//    {
//        return this.ProjectID;
//    }
//
//    public HashMap<Integer, String> getlistProjects()
//    {
//        return listToAddProjects;
//    }
//
//    public ArrayList<Date> getlistDates()
//    {
//        return listToAddDates;
//    }
}
