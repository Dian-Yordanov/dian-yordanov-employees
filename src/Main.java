import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    public static String textFileUrlLocal = System.getProperty("user.dir") + "\\textFile\\file1.txt";

    public static ArrayList<String> EmpID = new ArrayList<String>();
    public static ArrayList<String> ProjectID = new ArrayList<String>();
    public static ArrayList<Date> DateFrom = new ArrayList<Date>();
    public static ArrayList<Date> DateTo = new ArrayList<Date>();

    public static ArrayList<Employee> Employee = new ArrayList<Employee>();

    public static HashMap<String, ArrayList<String>> workerToProjectMapping = new HashMap<>();
    public static HashMap<String, ArrayList<String>> projectToWorkerMapping = new HashMap<>();
    public static HashMap<HashMap<Integer, String>, ArrayList<Date>> projectToTimeWorkedMapping = new HashMap<>();

    public static HashMap<Project, String> projectObjectList = new HashMap<>();

    public static String defaultDatePattern = "yyyy-MM-dd";

//    public static JTextField t1 = new JTextField();
    public static JTable jt = new JTable();
    public static JFrame mainJFrame;
    public static DefaultTableModel model;

    public static void main(String[] args) {

        System.out.println("Do you want to use the default text file or provide your own?");
        System.out.println("Press 'Enter' to use the default or give your own path.");
        System.out.println("To launch the gui, type 'GUI'");
        System.out.println("To exit program type 'Exit'");

        Scanner scanner = new Scanner(System.in);
        String readString = scanner.nextLine();

        // The boolean is needed for the first input to work
        boolean executed = false;

        if(readString.equals("Exit")){
            System.out.println("Ending program");
        }
        while(readString!=null && !readString.equals("Exit")) {

            if (readString.isEmpty()) {
                // This is where the program is executed using default path
                System.out.println(textFileUrlLocal + " is default path");
                runProgramCodeWithGivenPath(textFileUrlLocal);
                break;
            }
            else if(!executed){
                if(isValidPath(readString)){
                    System.out.println("Valid path, executing the program");
                    runProgramCodeWithGivenPath(readString);
                    break;
                }
                else if(readString.equals("GUI")){
                    launchGui();
                }
                else{
                    System.out.println("Path: " + readString + " is not valid");
                    System.out.println("Example of a valid path string would be:");
                    System.out.println("C:\\Users\\User\\Desktop\\Sirma\\textFile\\file1.txt");
                }
                executed = true;
            }

            if (scanner.hasNextLine()) {
                readString = scanner.nextLine();
                if(readString.equals("Exit")){
                    System.out.println("Ending program");
                    break;
                }
                else if(readString.equals("GUI")){
                    launchGui();
                }
                else {
                    if(isValidPath(readString)){
                        System.out.println("Valid path, executing the program");
                        runProgramCodeWithGivenPath(readString);
                        break;
                    }
                    else{
                        System.out.println("Path: " + readString + " is not valid");
                        System.out.println("Example of a valid path string would be:");
                        System.out.println("C:\\Users\\User\\Desktop\\Sirma\\textFile\\file1.txt");
                    }
                }
            } else {
                readString = null;
            }
        }
    }

    public static void launchGui(){

        mainJFrame=new JFrame("Sirma task");

        model = new DefaultTableModel();
        JTable jt=new JTable(model);

        model.addColumn("Employee ID #1, Employee ID #2");
        model.addColumn("Project ID");
        model.addColumn("Days worked");

        JScrollPane sp=new JScrollPane(jt);

        JButton b = new JButton("Select file path");
        b.setBounds(10,400,150,20);
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

//                System.out.println(launchFilePicker());
                runProgramCodeWithGivenPath(launchFilePicker());

            }
        });
        mainJFrame.add(b);

        mainJFrame.add(sp);
        mainJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainJFrame.setSize(700,500);
        mainJFrame.setVisible(true);

    }

    private static String launchFilePicker(){
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        File selectedFile = null;

        jfc.setDialogTitle("Select file");
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text file", "txt");
        jfc.addChoosableFileFilter(filter);

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = jfc.getSelectedFile();
        }

        System.out.println("File selected: " + selectedFile.getAbsolutePath());
        return selectedFile.getAbsolutePath();
    }

    /**
    Runs the main portion of the program but with a given path string
    */
    private static void runProgramCodeWithGivenPath(String path){
        System.out.println("Selected file path: " + path);
        getFileContent(path);

//        scanFileContent(getFileContent(path));
//        getFileContent(path)
//        System.out.println(" getFileContent(path): " + getFileContent(path));

        ArrayList<String> checkForDuplicatesinTheSet = new ArrayList<>();
        ArrayList<String> checkForDuplicatesinTheSetNumber = new ArrayList<>();
        ArrayList<Long> checkForDuplicatesinTheSetPeriod = new ArrayList<>();

        // First loop used for mapping
        for(int i=0;i<EmpID.size();i++){
            HashMap<Integer, String> listToAddProjects = new HashMap<>();
            listToAddProjects.put(i, ProjectID.get(i));

            ArrayList<Date> listToAddDates = new ArrayList<Date>();
            listToAddDates.add(DateFrom.get(i));
            listToAddDates.add(DateTo.get(i));

            projectToTimeWorkedMapping.put(listToAddProjects, listToAddDates );
            projectObjectList.put(new Project(i, ProjectID.get(i), listToAddDates), ProjectID.get(i));
        }

        //  This nested loop is used to map different values in a way that can be used later to further map them
        for (Map.Entry<String, ArrayList<String>> i : workerToProjectMapping.entrySet()) {
            for (Map.Entry<String, ArrayList<String>> ii : projectToWorkerMapping.entrySet()) {
                if(ii.getValue().contains(i.getKey())) {
                    String stringOfJobCollaborations = String.valueOf(ii.getValue());
                    stringOfJobCollaborations = stringOfJobCollaborations.substring(1, stringOfJobCollaborations.length() - 1);
//                    System.out.println("arrayOfJobCollaborations " + stringOfJobCollaborations);
//                    System.out.println("arrayOfJobCollaborations " + stringOfJobCollaborations);
                    String[] arrayOfJobCollaborations = stringOfJobCollaborations.split(", ");
                    for(int iii = 0;iii<arrayOfJobCollaborations.length;iii++) {
                        for (int iiii = 0; iiii < arrayOfJobCollaborations.length; iiii++) {
                            String employeeSplitValue1 = arrayOfJobCollaborations[iii];
                            String employeeSplitValue2 = arrayOfJobCollaborations[iiii];



                            String projectNameString = ii.getKey();

//                            System.out.println("projectNameString " + projectNameString);
//                            System.out.println("employeeSplitValue1 " + employeeSplitValue1);
//                            System.out.println("ProjectID.indexOf(projectNameString) " + ProjectID.indexOf(projectNameString));

                            ArrayList<Integer> arrayOfIndixes = new ArrayList<Integer>();
                            for (Map.Entry<Project, String> entry : projectObjectList.entrySet()) {
                                if (entry.getValue().equals(projectNameString)) {
                                    arrayOfIndixes.add(entry.getKey().getI());
                                }
                            }

//                            System.out.println(arrayOfIndixes);
//                            System.out.println("Employee.get(ProjectID.indexOf(projectNameString)) " + Employee.get(ProjectID.indexOf(projectNameString)));

                            String permutation1 = employeeSplitValue1 + " " + employeeSplitValue2 + " "+  projectNameString;
                            String permutation2 = employeeSplitValue2 + " " + employeeSplitValue1 + " " +  projectNameString ;

                            if (!checkForDuplicatesinTheSet.contains(permutation1)
                                    && !checkForDuplicatesinTheSet.contains(permutation2)
                                    && !employeeSplitValue1.equals(employeeSplitValue2)){

                                        checkForDuplicatesinTheSet.add(permutation1);
                                        checkForDuplicatesinTheSetNumber.add(projectNameString);

                                        checkForDuplicatesinTheSetPeriod.add(getPeriodBetweenStAndEnd(
                                                Employee.get(arrayOfIndixes.get(0)), Employee.get(arrayOfIndixes.get(1))));
                            }
                        }
                    }
                }
            }
        }

        // Once the big nested loop is done we have our mappings and need to further map values a bit
        ArrayList<EmployeeCollaboration> arrayOfEmployeesCollaborations = new ArrayList<>();
        ArrayList<String> listOfEmployeeColaborations = new ArrayList<>();

        int index = 0;
        HashMap<Integer, String> mappingOfEmployeesAndThierCollaborations = new HashMap<>();

        for (int i=0;i<checkForDuplicatesinTheSet.size();i++){
            String values = checkForDuplicatesinTheSet.get(i);

            String employee1 = values.split(" ")[0];
            String employee2 = values.split(" ")[1];
            String employees = employee1 + " " + employee2;

            //            System.out.println("employees " + employees +
            //                               " worked on project" + checkForDuplicatesinTheSetNumber.get(i) +
            //                               " for a period of " + checkForDuplicatesinTheSetPeriod.get(i) + " days");

            mappingOfEmployeesAndThierCollaborations.put(index, employees);

            if(!listOfEmployeeColaborations.contains(employees)){
                listOfEmployeeColaborations.add(employees);
                EmployeeCollaboration collaboration = new EmployeeCollaboration(employees,checkForDuplicatesinTheSetNumber.get(i),checkForDuplicatesinTheSetPeriod.get(i));
                arrayOfEmployeesCollaborations.add(collaboration);
            }
            else{
                arrayOfEmployeesCollaborations.get(listOfEmployeeColaborations.indexOf(mappingOfEmployeesAndThierCollaborations.get(index))).addProjectsworkedOn(checkForDuplicatesinTheSetNumber.get(i));
                arrayOfEmployeesCollaborations.get(listOfEmployeeColaborations.indexOf(mappingOfEmployeesAndThierCollaborations.get(index))).addPeriodBeingWorkedOn(checkForDuplicatesinTheSetPeriod.get(i));
            }
            index++;
        }

        //  Almost done with mapping, also doing the sum of time worked periods here
        HashMap<String, Long> mappingOfPairsToTheirCombinedTimeWorkedTogether = new HashMap<>();

        for (EmployeeCollaboration i : arrayOfEmployeesCollaborations) {
            long sum = sum(i.getperiodBeingWorkedOn());
            System.out.println("pair " + i.getemployeesCollaboration() + " worked on these projects: "
                    + i.getprojectBeingWorkedOn() + " for these periods of time: " + i.getperiodBeingWorkedOn()
                    + " for a total time of " + sum );
            try {
                model.addRow(new Object[]{i.getemployeesCollaboration(), i.getprojectBeingWorkedOn(), i.getperiodBeingWorkedOn()});
            }
            catch (NullPointerException e) {
            }
            mappingOfPairsToTheirCombinedTimeWorkedTogether.put(i.getemployeesCollaboration(), sum);
        }

        // mappingOfPairsToTheirCombinedTimeWorkedTogether is our final mapping target as it maps
        // different employee pairs to their sum of time worked together. Basically we just need to sort the resulted
        // HashMap results to get the required task solution.

        System.out.println("");
        System.out.println("After sorting ascending order......");
        boolean ASC = true;
        boolean DESC = false;
        LinkedHashMap<String, Long> sortedMapAsc = sortByValue(mappingOfPairsToTheirCombinedTimeWorkedTogether, DESC);
        printMap(sortedMapAsc);

        Map.Entry<String,Long> entry = sortedMapAsc.entrySet().iterator().next();
        String key = entry.getKey();
        Long value = entry.getValue();
        System.out.println("The pair with most time spend working together on projects is: " + key +
                " - Time spend working together : a total amount of days: " + value +
                " | Detailed distribution of collaboration time: " + convertDateOfDays(value));



    }

    /**
     given 4 dates (start date for employee 1, end date for employee 1, start date for employee 2, end date for employee 2)
     find the difference between them.
     For example:
     employee 1 and 2 both work on project A;
     employee 1 start at 1.1.2021 and ends at 4.1.2021, employee 2 starts at 3.1.2021 and end at 5.1.2021
     The difference would be 1 day, considering that the bigger of the start dates is taken and "removed"
     from the smaller of the end dates.
     Note that because it is possible for a person to work on a project continuously while another has started
     and finished and started and finished again, this will have to be called for each date from person 1 to
     each date from person 2.
     */
    private static long getPeriodBetweenStAndEnd(Employee Employee1, Employee Employee2 ){

        long timeDifference = 0;
        Date biggerStartDate = null;
        Date smallerEndDate = null;

//        System.out.println("Employee1.getDateFrom().getTime(): " + Employee1.getDateFrom());
//        System.out.println("Employee2.getDateFrom().getTime(): " + Employee2.getDateFrom());
//
//        System.out.println("Employee1.getDateTo().getTime(): " + Employee1.getDateTo());
//        System.out.println("Employee2.getDateTo().getTime(): " + Employee2.getDateTo());


        if(Employee1.getDateFrom().getTime() - Employee2.getDateFrom().getTime() >= 0){
            biggerStartDate = Employee1.getDateFrom();
        }
        else if(Employee2.getDateFrom().getTime() - Employee1.getDateFrom().getTime() > 0){
            biggerStartDate = Employee2.getDateFrom();
        }

        if(Employee2.getDateTo().getTime() - Employee1.getDateTo().getTime() >= 0){
            smallerEndDate = Employee1.getDateTo();
        }
        else if(Employee1.getDateTo().getTime() - Employee2.getDateTo().getTime() > 0){
            smallerEndDate = Employee2.getDateTo();
        }


        timeDifference = smallerEndDate.getTime() - biggerStartDate.getTime();

        timeDifference = TimeUnit.MILLISECONDS.toDays(timeDifference);

        if(timeDifference >= 0)
        {
//            System.out.println ("start period of collaborative work: " + biggerStartDate + " end period of collaborative work: " + smallerEndDate);
            return timeDifference;
        }
        else {
//            System.out.println ("there is no period of collaborative work");
            timeDifference = 0;
        }


//        long diff = start2.getTime() - start1.getTime();
//        System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

        return timeDifference;
    }

     /**
     Reads file content and returns a string.
     For a bigger CVS file other methods of reading from a file may be used.
     Given that we will be using a Scanner, this can be done in less code by doing the file
     scanning in the scanner directly.
     */
    private static String getFileContent(String fileUrl){
        String textFileContent = "";
        File file = new File(fileUrl);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            int lineNumber = 0;
            while((st=br.readLine()) != null){

                //  The next lines of code aim at normalising date input and also making
                //  it possible to use as many date formats as possible.
                //  Basically, you can use dates with any special characters as separators,
                //  do not need to care for '01' vs '1' date format, can use almost
                //  any date formatting for as long as it creates a correct date.
                //  There is one very notable exception for date format usage and that would be when
                //  the year is in the middle: because day and month can be on any side of it and for as long
                //  as days are in the range of {0,12} and months are in the same range, they can both be correct so
                //  only "MM-yyyy-dd" and "MM/yyyy/dd" are excepted
                //  although "MM/yyyy/dd" and other special character separation will be transferred to "MM-yyyy-dd".
                //  I had the idea of making it possible to select a default date pattern from GUI or CLI
                //  and thus making it possible to counter this problem but it creates new problems and I may not be able to fix it in time.

                String startDateString = ifNULLStringDateChecker(st.split(",")[2]);
                String endDateString = ifNULLStringDateChecker(st.split(",")[3]);

                startDateString = dateStringNormalisationFor1CharacterInput(startDateString);
                endDateString = dateStringNormalisationFor1CharacterInput(endDateString);

                SimpleDateFormat formatterStart = new SimpleDateFormat(determineDateFormat(startDateString), Locale.ENGLISH);
                SimpleDateFormat formatterEnd = new SimpleDateFormat(determineDateFormat(endDateString), Locale.ENGLISH);

                Date startDate = formatterStart.parse(startDateString);
                Date endDate = formatterEnd.parse(endDateString);

                EmpID.add(st.split(",")[0]);
                ProjectID.add(st.split(",")[1]);
                DateFrom.add(startDate);
                DateTo.add(endDate);

                addValues(st.split(",")[0], st.split(",")[1], workerToProjectMapping);
                addValues(st.split(",")[1], st.split(",")[0], projectToWorkerMapping);

                Employee.add(new Employee(st.split(",")[0], st.split(",")[1], startDate, endDate));

                lineNumber++;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return textFileContent;
    }

    /**
     The way the task is given, it is obvious that the file format given is a CSV (Comma-separated values ) file format
     and there are a few 3rd party libraries for reading it like http://super-csv.github.io/super-csv/index.html or
     http://commons.apache.org/proper/commons-csv/ .
     These libraries are efficient and probably better to be used usually but I will write my own implementation for the task.
     I will just loop over the string and put in different row values in the different columns in different arrays.
     There are a few other possible ways to do this, but for the time being I am going with this approach.
     */
    private static void scanFileContent(String fileContent){
        Scanner sc = null;

        sc = new Scanner(fileContent);
        sc.useDelimiter(",");
        while (sc.hasNext())
        {
            System.out.print(sc.next());
        }
        sc.close();
    }

    /**
     checks if the string provided is NULL or correct, if yes - returns it to be made a date,
     otherwise provides current day Date
     */
    private static String ifNULLStringDateChecker(String dateStringToCheckIfNULL){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(defaultDatePattern);
        LocalDateTime now = LocalDateTime.now();

        String checkedDate = formatter.format(now);

        if(dateStringToCheckIfNULL.equals(" NULL")){
            return checkedDate;
        }
        else {
            return dateStringToCheckIfNULL;
        }
    }

    /**
     * Checks if a string is a valid path.
     * Null safe.
     *
     * Calling examples:
     *    isValidPath("c:/test");      //returns true
     *    isValidPath("c:/te:t");      //returns false
     *    isValidPath("c:/te?t");      //returns false
     *    isValidPath("c/te*t");       //returns false
     *    isValidPath("good.txt");     //returns true
     *    isValidPath("not|good.txt"); //returns false
     *    isValidPath("not:good.txt"); //returns false
     */
    public static boolean isValidPath(String path) {
        if (path.matches(".*[/\n\r\t\0\f`?*\\<>|\":].*")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    /**
    custom function to add values that can be duplicated to the hash maps.
     */
    private static void addValues(String key, String value, HashMap<String, ArrayList<String>> hashMapMapping) {
        ArrayList tempList = null;
        if (hashMapMapping.containsKey(key)) {
            tempList = hashMapMapping.get(key);
            if(tempList == null)
                tempList = new ArrayList();
            tempList.add(value);
        } else {
            tempList = new ArrayList();
            tempList.add(value);
        }
        hashMapMapping.put(key,tempList);
    }

    /**
     Find the sum of all elements in a list.
     */
    public static long sum(List<Long> list) {
        long sum = 0;

        for (long i : list)
            sum = sum + i;

        return sum;
    }

    /**
     Used to sort the final HashMap.
     */
    private static LinkedHashMap<String, Long> sortByValue(Map<String, Long> unsortMap, final boolean order) {
        List<Map.Entry<String, Long>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));

    }

    private static void printMap(Map<String, Long> map) {
        map.forEach((key, value) -> System.out.println("Pair : " + key +
                " - Time spend working together : a total amount of days: " + value +
                " | Detailed distribution of collaboration time: " + convertDateOfDays(value)));
    }

    /**
     This checks if a string is suitable for a date format of a type containing a year, month and a date format
     It splits the string on 3 parts using different separators, if the split fails it is not a valid date format
     If the 3 parts do not contain 2 with 2 int chars and 1 with 4 int chars, it also fails.
     Please note that the program operates under the assumption that the date is given only contains Years, months and days
     Where days are the smallest unit of time. It would not be hard to include minutes and hours and assume
     that if they are not inputted they are 00:00 , but given that it not a requirement,
     I have decided to go with years, months, days  where the separator can be any special character.
     */
    private static boolean checkIfDateFormatGivenIsCorrect(String dateStringToCheck){
        boolean stringIsCorrect = false;

        boolean has1pieceWith4Characters = false;
        boolean has2piecesWith2CharactersPiece1 = false;
        boolean has2piecesWith2CharactersPiece2 = false;

        try {
            // Get the 3 pieces:
            ArrayList<String> arrayFromList = new ArrayList<String>(Arrays.asList(dateStringToCheck.split("[^a-zA-Z0-9]")));
            System.out.println(arrayFromList.get(0) + " " + arrayFromList.get(1) + " " + arrayFromList.get(2));

            // Check if 1 piece is 4 chars and 2 pieces are 2 chars
            for (String patternPiece : arrayFromList)
            {
                if (patternPiece.length() == 4){
                    has1pieceWith4Characters = true;
                }
                if (patternPiece.length() == 2 && !has2piecesWith2CharactersPiece1){
                    has2piecesWith2CharactersPiece1 = true;
                } else if (patternPiece.length() == 2){
                    has2piecesWith2CharactersPiece2 = true;
                }
            }

            if(has1pieceWith4Characters && has2piecesWith2CharactersPiece1 && has2piecesWith2CharactersPiece2){
                System.out.println("Your date pattern is correct");
                stringIsCorrect = true;
            }
            else {
                System.out.println("Your date pattern is incorrect: it should have a 4 character long year and 2 2 character long days, months pieces.");
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("String entered is: " + dateStringToCheck);
            System.out.println("There must be an error with the date pattern you entered");
        }

        return stringIsCorrect;
    }

    /**
    The purpose of this method is to check if a dateString is correct but also normalise it if it has day/month
     input with 1 character only. For example: 2021-1-4 is changed into 2021-01-04
     */
    private static String dateStringNormalisationFor1CharacterInput(String stringInput){
        stringInput = stringInput.trim();
        ArrayList<String> arrayFromList = new ArrayList<String>(Arrays.asList(stringInput.split("[^a-zA-Z0-9]")));
//        System.out.println("split: " + arrayFromList.get(0) + " " + arrayFromList.get(1) + " " + arrayFromList.get(2));
        String returnString = "";

        for (String patternPiece : arrayFromList)
        {
            if (patternPiece.length() == 1){
                patternPiece = "0" + patternPiece;
            }
            returnString = returnString + "-" + patternPiece ;
        }

        returnString = returnString.substring(1,returnString.length());

//        System.out.println("return string: " + returnString);

        return returnString;
    }

    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
        put("^\\d{8}$", "yyyyMMdd");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
        put("^\\d{1,2}-\\d{4}-\\d{1,2}$", "MM-yyyy-dd");
        put("^\\d{1,2}/\\d{4}/\\d{1,2}$", "MM/yyyy/dd");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
        put("^\\d{12}$", "yyyyMMddHHmm");
        put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
        put("^\\d{14}$", "yyyyMMddHHmmss");
        put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
    }};

    /**
     * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
     * format is unknown. You can simply extend DateUtil with more formats if needed.
     * @param dateString The date string to determine the SimpleDateFormat pattern for.
     * @return The matching SimpleDateFormat pattern, or null if format is unknown.
     * @see SimpleDateFormat
     */
    public static String determineDateFormat(String dateString) {
        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return DATE_FORMAT_REGEXPS.get(regexp);
            }
        }
        return null; // Unknown format.
    }

    /**
    converts a number of days long variable into a years, months, weeks and days string.
     */
    private static String convertDateOfDays(Long noOfDays){
        String returnedString = "";
        long year, month, week;

        year = noOfDays/365;
        noOfDays=noOfDays%365;

        month = noOfDays/30;
        noOfDays=noOfDays%30;

        week = noOfDays/7;
        noOfDays=noOfDays%7;

        returnedString = "Year: " + year + " Month: " + month + " Week: " + week + " Day: " + noOfDays;
        return returnedString;
    }
}
