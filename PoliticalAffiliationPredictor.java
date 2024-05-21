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

        // Data storage for current user responses
        Map<String, Integer> userResponses = new HashMap<>();

        for (String[] questionSet : questions) {
            System.out.println(questionSet[0]); // Print the question
            for (int i = 1; i < questionSet.length; i++) {
                System.out.println(questionSet[i]); // Print the answer options
            }
            String answer;
            // Validate input to ensure it's one of the expected options (A, B, C, or D)
            while (true) {
                System.out.print("Your answer: ");
                answer = scanner.nextLine().trim().toUpperCase();
                if (answer.matches("[A-D]")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter A, B, C, or D.");
                }
            }
            recordResponse(answer, questionSet[0], userResponses); // Record response
        }

        // Final question to determine user's self-identified political affiliation
        System.out.println("Which political party do you affiliate with?");
        for (int i = 0; i < parties.length; i++) {
            System.out.println((i+1) + ". " + parties[i]);
        }
        int affiliation = 0;
        while (true) {
            System.out.print("Your answer (1-4): ");
            affiliation = scanner.nextInt();
            if (affiliation >= 1 && affiliation <= 4) {
                break;
            } else {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
            }
        }
        String userParty = parties[affiliation - 1];
        System.out.println("Thank you for participating in the survey!");

        // Store raw responses
        try {
            storeRawResponses(userResponses, userParty);
        } catch (IOException e) {
            System.out.println("An error occurred while writing data to files.");
            e.printStackTrace();
        }

        // Predict political affiliation based on collected data
        String predictedParty = predictParty(userResponses);
        return predictedParty;
    }

    // Method to record survey response for a political party
    private void recordResponse(String answer, String question, Map<String, Integer> userResponses) {
        // Save the raw response
        userResponses.put(answer, userResponses.getOrDefault(answer, 0) + 1);
    }

    // Method to store raw responses
    private void storeRawResponses(Map<String, Integer> userResponses, String userParty) throws IOException {
        try (FileWriter writer = new FileWriter("responses.txt", true)) {
            writer.write("User Party: " + userParty + "\n");
            for (Map.Entry<String, Integer> entry : userResponses.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            writer.write("\n");
        }
    }

    // Method to predict political affiliation based on collected data
    private String predictParty(Map<String, Integer> userResponses) {
        String predictedParty = "";
        double maxProbability = 0;
        for (String party : parties) {
            double probability = calculateProbability(party, userResponses);
            if (probability > maxProbability) {
                maxProbability = probability;
                predictedParty = party;
            }
        }
        return predictedParty;
    }

    // Method to calculate probability using a basic Naive Bayes classifier
    private double calculateProbability(String party, Map<String, Integer> userResponses) {
        double probability = 1.0;
        Map<String, Integer> partyResponses = partyData.get(party);
        for (Map.Entry<String, Integer> entry : userResponses.entrySet()) {
            String answer = entry.getKey();
            int count = entry.getValue();
            int total = partyResponses.getOrDefault(answer, 0) + 1; // Add 1 for smoothing
            probability *= (double) total / (partyResponses.size() + 1); // Adjust for smoothing
        }
        return probability;
    }

    public static void main(String[] args) {
        PoliticalAffiliationPredictor predictor = new PoliticalAffiliationPredictor();
        String predictedParty = predictor.conductSurveyAndPredict();
        System.out.println("Based on your responses, your predicted political affiliation is: " + predictedParty);
    }
}
