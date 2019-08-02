package Controllers.Admins;

import Controllers.Super;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ReportsClass extends Super {
    public void hospitalStatistics() {

    }

    public void patientHistory(String email) {

    }

    /**
     * calculate disease statistics for patients at various  locations
     * DIAGNOSIS--------NUMBER OF PATIENTS------LOCATION
     * <p>
     * CONCLUSION FROM ABOVE TABLE
     */
    public void diseaseStats() {
        int max = 0;
        String maxdiseaseName = "";
        HashMap<String, Integer> hashMap = new HashMap<>();
        StringBuilder prescriptions = new StringBuilder();
        StringBuilder diseases = new StringBuilder();
        PrintWriter outfile = null;
        try {
            outfile = new PrintWriter("Report.txt");//output file
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM prescriptions");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    prescriptions.append(resultSet.getString("diagnosis"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
//            System.out.println(prescriptions.toString() + " is the builder");
            PreparedStatement preparedStatement = connection.prepareStatement("select * from disease_database");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    int count = 0, start = 0;

                    diseases.append(resultSet.getString("disease"));
                    while ((start = prescriptions.indexOf(resultSet.getString("disease"), start)) != -1) {
                        start++;
                        count++;
                    }
                    hashMap.put(resultSet.getString("disease"), count);
//                    System.out.println("disease=" + resultSet.getString("disease") + " counte= " + count + " Index= " + start);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayList<String> maximumValues = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM disease_database");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                outfile.println("DISEASES AND THEIR FREQUENCY OF OCCURRENCE\n**********************************");
                while (resultSet.next()) {
                    try {
                        int current = hashMap.get(resultSet.getString("disease"));
                        if (current >= max) {
                            if (current > max) {
                                maximumValues.clear();
                            }
                            maximumValues.add(resultSet.getString("disease"));
                            maxdiseaseName = resultSet.getString("disease");
                            max = current;
                            outfile.println(resultSet.getString("disease") + ":\t" + hashMap.get(resultSet.getString("disease")));

                        } else {
                            outfile.println(resultSet.getString("disease") + ":\t" + hashMap.get(resultSet.getString("disease")));

                        }

//                        System.out.println(resultSet.getString("disease")+"::"+hashMap.get(resultSet.getString("disease")));

                    } catch (Exception ignore) {

                    }
                }
                outfile.println("\nCONCLUSION\nTHE MOST PREVALENT DISEASES ARE:");//+maxdiseaseName +" WHICH INFECTED "+max+" PEOPLE");
                int count = 1;
                for (String disease : maximumValues
                ) {
                    if (count == 1) {
                        outfile.print(disease.toUpperCase());

                    } else if (count < maximumValues.size()) {
                        outfile.print("," + disease.toUpperCase());

                    } else {
                        outfile.print(" AND " + disease.toUpperCase());

                    }
                    count++;
                }
                outfile.print(" WHICH INFECTED " + max + " PEOPLE");
                outfile.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void conditionStats() {

    }
}
