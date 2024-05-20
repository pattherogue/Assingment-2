import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;

public class PoliticalAffiliationPredictor {

    // Data storage for survey responses of each political party
    private Map<String, Map<String, Integer>> partyData;
    private static final String[] parties = {"Republican", "Democrat", "Libertarian", "Green"};

    // Initialize questions
    private static final String[][] questions = {
        {
            "What should the government do to help the poor?",
            "A. Make it easier to apply for assistance.",
            "B. Allow parents to use education funds for charter schools.",
            "C. Create welfare to work programs.",
            "D. Nothing."
        },
        {
            "What is your stance on environmental protection?",
            "A. Implement strict regulations to reduce carbon emissions.",
            "B. Incentivize businesses to adopt green practices.",
            "C. Focus on technological innovations to solve environmental issues.",
            "D. The market will naturally address environmental concerns."
        },
        {
            "What is your stance on healthcare?",
            "A. Healthcare should be universal and government-funded.",
            "B. Introduce market-based reforms to increase competition.",
            "C. Provide tax incentives for private health savings.",
            "D. Reduce government involvement in healthcare."
        }
    };

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
        System.out.println("Welcome to the Political Affiliation Predictor Survey!");
        
        for (String[] questionSet : questions) {
            System.out.println(questionSet[0]); // Print the question
            for (int i = 1; i < questionSet.length; i++) {
                System.out.println(questionSet[i]); // Print the answer options
            }
            System.out.print("Your answer: ");
            String answer = scanner.nextLine().trim().toUpperCase();
            recordResponse(answer, questionSet[0]); // Record response
        }

        // Predict political affiliation based on collected data
        String predictedParty = predictParty();
        return predictedParty;
    }

    // Method to record survey response for a political party
    private void recordResponse(String answer, String question) {
        // Iterate through parties and assign weights
        for (String party : parties) {
            Map<String, Integer> partyResponses = partyData.get(party);
            int weight = determineWeight(answer, party);
            partyResponses.put(question, partyResponses.getOrDefault(question, 0) + weight);
        }
    }

    // Method to determine weight based on answer and party
    private int determineWeight(String answer, String party) {
        // Customize weights for each answer and party
        switch (party) {
            case "Republican":
                return answer.equals("D") ? 3 : (answer.equals("B") ? 2 : 1);
            case "Democrat":
                return answer.equals("A") ? 3 : (answer.equals("C") ? 2 : 1);
            case "Libertarian":
                return answer.equals("C") ? 3 : (answer.equals("D") ? 2 : 1);
            case "Green":
                return answer.equals("A") ? 3 : (answer.equals("B") ? 2 : 1);
            default:
                return 1;
        }
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
