package data_access;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import use_case.dataset.CurrentTableGateway;

import java.util.Arrays;
import java.util.List;

/**
 * Complex Sample Data with multiple categorical and numerical columns
 */
public class SampleDataLoader {
    public static void loadSampleData(CurrentTableGateway gateway) {
        List<DataRow> rows = Arrays.asList(
                new DataRow(Arrays.asList("John Smith", "25", "Engineer", "New York", "85000", "Male", "Bachelor", "5", "Technology", "Yes", "4.2", "Single")),
                new DataRow(Arrays.asList("Jane Doe", "30", "Doctor", "Boston", "250000", "Female", "Doctorate", "8", "Healthcare", "Yes", "4.8", "Married")),
                new DataRow(Arrays.asList("Bob Johnson", "28", "Teacher", "Chicago", "45000", "Male", "Master", "3", "Education", "No", "3.9", "Single")),
                new DataRow(Arrays.asList("Alice Williams", "32", "Designer", "Seattle", "120000", "Female", "Bachelor", "7", "Technology", "Yes", "4.5", "Married")),
                new DataRow(Arrays.asList("Charlie Brown", "27", "Developer", "Austin", "95000", "Male", "Bachelor", "4", "Technology", "Yes", "4.3", "Single")),
                new DataRow(Arrays.asList("Diana Prince", "35", "Manager", "San Francisco", "180000", "Female", "Master", "12", "Business", "Yes", "4.7", "Married")),
                new DataRow(Arrays.asList("Ethan Hunt", "29", "Analyst", "Denver", "75000", "Male", "Bachelor", "5", "Finance", "No", "4.0", "Single")),
                new DataRow(Arrays.asList("Fiona Green", "31", "Scientist", "Portland", "105000", "Female", "Doctorate", "6", "Research", "Yes", "4.6", "Married")),
                new DataRow(Arrays.asList("George Clark", "26", "Artist", "Los Angeles", "62000", "Male", "Bachelor", "2", "Arts", "No", "3.8", "Single")),
                new DataRow(Arrays.asList("Hannah Lee", "33", "Consultant", "Miami", "145000", "Female", "Master", "9", "Business", "Yes", "4.4", "Married")),
                new DataRow(Arrays.asList("Ian Davis", "24", "Intern", "Atlanta", "35000", "Male", "Bachelor", "1", "Technology", "No", "3.5", "Single")),
                new DataRow(Arrays.asList("Julia Roberts", "36", "Executive", "Dallas", "220000", "Female", "Master", "14", "Business", "Yes", "4.9", "Married")),
                new DataRow(Arrays.asList("Kevin White", "28", "Engineer", "Phoenix", "88000", "Male", "Master", "5", "Technology", "Yes", "4.1", "Single")),
                new DataRow(Arrays.asList("Laura Martinez", "30", "Lawyer", "Houston", "165000", "Female", "Doctorate", "7", "Legal", "Yes", "4.6", "Married")),
                new DataRow(Arrays.asList("Michael Scott", "34", "Sales Rep", "Philadelphia", "70000", "Male", "Bachelor", "10", "Sales", "No", "3.7", "Single"))
        );

        List<Column> columns = Arrays.asList(
                new Column(Arrays.asList("John Smith", "Jane Doe", "Bob Johnson", "Alice Williams", "Charlie Brown",
                        "Diana Prince", "Ethan Hunt", "Fiona Green", "George Clark", "Hannah Lee",
                        "Ian Davis", "Julia Roberts", "Kevin White", "Laura Martinez", "Michael Scott"),
                        DataType.CATEGORICAL, "Name"),
                
                new Column(Arrays.asList("25", "30", "28", "32", "27", "35", "29", "31", "26", "33", "24", "36", "28", "30", "34"),
                        DataType.NUMERIC, "Age"),
                
                new Column(Arrays.asList("Engineer", "Doctor", "Teacher", "Designer", "Developer",
                        "Manager", "Analyst", "Scientist", "Artist", "Consultant",
                        "Intern", "Executive", "Engineer", "Lawyer", "Sales Rep"),
                        DataType.CATEGORICAL, "Occupation"),
                
                new Column(Arrays.asList("New York", "Boston", "Chicago", "Seattle", "Austin",
                        "San Francisco", "Denver", "Portland", "Los Angeles", "Miami",
                        "Atlanta", "Dallas", "Phoenix", "Houston", "Philadelphia"),
                        DataType.CATEGORICAL, "Location"),
                
                new Column(Arrays.asList("85000", "250000", "45000", "120000", "95000",
                        "180000", "75000", "105000", "62000", "145000",
                        "35000", "220000", "88000", "165000", "70000"),
                        DataType.NUMERIC, "Salary"),
                
                new Column(Arrays.asList("Male", "Female", "Male", "Female", "Male",
                        "Female", "Male", "Female", "Male", "Female",
                        "Male", "Female", "Male", "Female", "Male"),
                        DataType.CATEGORICAL, "Gender"),
                
                new Column(Arrays.asList("Bachelor", "Doctorate", "Master", "Bachelor", "Bachelor",
                        "Master", "Bachelor", "Doctorate", "Bachelor", "Master",
                        "Bachelor", "Master", "Master", "Doctorate", "Bachelor"),
                        DataType.CATEGORICAL, "Education"),
                
                new Column(Arrays.asList("5", "8", "3", "7", "4", "12", "5", "6", "2", "9", "1", "14", "5", "7", "10"),
                        DataType.NUMERIC, "Years Experience"),
                
                new Column(Arrays.asList("Technology", "Healthcare", "Education", "Technology", "Technology",
                        "Business", "Finance", "Research", "Arts", "Business",
                        "Technology", "Business", "Technology", "Legal", "Sales"),
                        DataType.CATEGORICAL, "Industry"),
                
                new Column(Arrays.asList("Yes", "Yes", "No", "Yes", "Yes", "Yes", "No", "Yes", "No", "Yes",
                        "No", "Yes", "Yes", "Yes", "No"),
                        DataType.CATEGORICAL, "Remote Work"),
                
                new Column(Arrays.asList("4.2", "4.8", "3.9", "4.5", "4.3", "4.7", "4.0", "4.6", "3.8", "4.4",
                        "3.5", "4.9", "4.1", "4.6", "3.7"),
                        DataType.NUMERIC, "Performance Rating"),
                
                new Column(Arrays.asList("Single", "Married", "Single", "Married", "Single",
                        "Married", "Single", "Married", "Single", "Married",
                        "Single", "Married", "Single", "Married", "Single"),
                        DataType.CATEGORICAL, "Marital Status")
        );

        DataSet sampleData = new DataSet(rows, columns);
        gateway.save(sampleData);
    }
}