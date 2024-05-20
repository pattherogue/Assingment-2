import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;

public class PoliticalAffiliationPredictor {
    
    // Data storage for survey responses of each political party
    private Map<String, Map<String, Integer>> partyData;
    private static final String[] parties = {"Republican", "Democrat", "Libertarian", "Green"};

    public PoliticalAffiliationPredictor() {
        // Initialize data storage
        partyData = new HashMap<>();
        for (String party : parties) {
            partyData.put(party, new HashMap<>());
        }
    }

    // Method to conduct survey and predict political affiliation
    public String conductSurveyAndPredict() {
        Scanner scanner = new Scanner(System.in);
        // Conduct survey
        System.out.println("Welcome to the Political Affiliation Predictor Survey!");
        for (int i = 0; i < parties.length; i++) {
            System.out.println("Please answer the following questions for " + parties[i] + ":");
            String[] questions = {"What should the government do to help the poor?",
                                  "What is your stance on environmental protection?",
                                  // Add more questions as needed
                                 };
            for (int j = 0; j < questions.length; j++) {
                System.out.println("Q" + (j+1) + ". " + questions[j]);
                System.out.print("Your answer: ");
                String answer = scanner.nextLine().toUpperCase();
                recordResponse(answer, parties[i], j+1); // Weighted response based on question number
            }
        }
        // Predict political affiliation based on collected data
        String predictedParty = predictParty();
        return predictedParty;
    }

    // Method to record survey response for a political party
    private void recordResponse(String answer, String party, int weight) {
        Map<String, Integer> partyResponses = partyData.get(party);
        partyResponses.put(answer, partyResponses.getOrDefault(answer, 0) + weight);
    }

    // Method to predict political affiliation based on collected data
    private String predictParty() {
        String predictedParty = "";
        int maxScore = 0;
        for (String party : parties) {
            int partyScore = calculatePartyScore(party);
            if (partyScore > maxScore) {
                maxScore = partyScore;
                predictedParty = party;
            }
        }
        return predictedParty;
    }

    // Method to calculate score for a political party based on collected data
    private int calculatePartyScore(String party) {
        int score = 0;
        Map<String, Integer> partyResponses = partyData.get(party);
        for (int value : partyResponses.values()) {
            score += value;
        }
        return score;
    }

    public static void main(String[] args) {
        PoliticalAffiliationPredictor predictor = new PoliticalAffiliationPredictor();
        String predictedParty = predictor.conductSurveyAndPredict();
        System.out.println("Based on your responses, your predicted political affiliation is: " + predictedParty);

        // Write survey responses to data files
        try {
            writeDataToFile(predictor.partyData);
        } catch (IOException e) {
            System.out.println("An error occurred while writing data to files.");
            e.printStackTrace();
        }
    }

    // Method to write survey responses to data files
    private static void writeDataToFile(Map<String, Map<String, Integer>> partyData) throws IOException {
        for (String party : parties) {
            Map<String, Integer> partyResponses = partyData.get(party);
            try (FileWriter writer = new FileWriter(party + "_data.txt")) {
                for (Map.Entry<String, Integer> entry : partyResponses.entrySet()) {
                    writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
                }
            }
        }
    }
}
