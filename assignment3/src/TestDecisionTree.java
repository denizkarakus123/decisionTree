import java.util.ArrayList;
import java.util.Arrays;

public class TestDecisionTree {

    public static void main(String[] args) {

        // local directory
        String localDir = System.getProperty("user.dir");
        
        //You might need to change these based on your operating system.
        String base = localDir + "/src/data/";
        
        //You might need to change these based on your operating system.
        String basedb = localDir + "/src/data/db/";

        boolean verbose = true ;

        try {

            DataReader dr = new DataReader();
            try {
                dr.read_data(basedb + "data_high_overlap.csv");
            } catch (Exception e) {
                e.printStackTrace();
                throw new AssertionError("[ERROR] Could not read csv file.");
            }
            // split the data into training and testing
            dr.splitTrainTestData(.5);

            // build a decision tree based on the data
            DecisionTree dt = DataReader.readSerializedTree(base + "data_high_overlap/thresh1.ser");
            if (dt == null) {
                throw new AssertionError("[ERROR] Could not read DT from file.");
            }

          /*
          		do your testing here
          */
        }
        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
